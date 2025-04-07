import pandas as pd
import aiohttp
import asyncio

# 구글 API 키 설정
API_KEY = ""  # 실제 API 키로 변경
SEARCH_URL = "https://places.googleapis.com/v1/places:searchText"

# CSV 파일 읽기
file_path = r"C:\Users\ASUS ROG\Desktop\babsim_with_coordinate.csv"
df = pd.read_csv(file_path, encoding="cp949")  # 이전에 cp949로 인코딩되었음(한글)

# 이런식으로 병렬로 처리하면 요청 빨리가능능
async def fetch_photo_url(session, photo_name):
    photo_url = f"https://places.googleapis.com/v1/{photo_name}/media?key={API_KEY}&maxHeightPx=1600"
    async with session.get(photo_url) as response:
        if response.status == 200:
            return photo_url
        else:
            print(f"사진 요청 실패: {response.status}")
            return None

# 이게 이전 get_openHourAndPhoto 파일에서 photoUrls컬럼을 받아왔으면 됐는데 실수로 못받아와서 다시 받아옴(원래 이전파일에서 처리해야할 로직직)
async def search_place(session, name, city, district):
    # 검색할 텍스트 쿼리 생성 (업소명 + 시도 + 시군)
    query = f"{name} {city} {district}"
    headers = {
        'Content-Type': 'application/json',
        'X-Goog-Api-Key': API_KEY,
        'X-Goog-FieldMask': '*'
    }
    data = {
        "textQuery": query,
    }
    
    async with session.post(SEARCH_URL, json=data, headers=headers) as response:
        return await response.json()

# 사진 URL을 저장할 컬럼 추가
df["photoUrls"] = ""

# 전체 항목 수와 처리 완료된 항목 수를 추적
total_items = len(df)
completed_items = 0

# 각 항목에 대해 장소 검색을 수행
async def process_places():
    global completed_items
    
    async with aiohttp.ClientSession() as session:  # ClientSession을 외부에서 생성
        for index, row in df.iterrows():
            name, city, district = row["업소명"], row["시도"], row["시군"]

            # 빈 값이 아니면 검색 시작
            if pd.notna(name) and pd.notna(city) and pd.notna(district):
                print(f"검색 중: {name} {city} {district}")

                # API 호출
                result = await search_place(session, name, city, district)

                # 성공적인 결과 처리
                if "places" in result and len(result["places"]) > 0:
                    place = result["places"][0]

                    # 사진 데이터 추출
                    photos = place.get('photos', [])
                    photo_urls = []
                    
                    # 비동기적으로 여러 요청을 동시에 처리
                    tasks = []
                    for photo in photos:
                        photo_name = photo.get('name', "")
                        if photo_name:
                            tasks.append(fetch_photo_url(session, photo_name))

                    # 사진 URL을 가져오고 리스트에 추가
                    completed_urls = await asyncio.gather(*tasks)
                    photo_urls = [url for url in completed_urls if url]

                    # 사진 URL을 컬럼에 추가
                    df.at[index, "photoUrls"] = ", ".join(photo_urls)

                    completed_items += 1  # 처리 완료된 항목 수 증가
                    print(f"완료: {name}, {city}, {district} - 사진 URL: {photo_urls}")
                else:
                    print(f"결과 없음: {name}, {city}, {district}")

            else:
                print(f"정보 부족: {name} {city} {district}")

            # 진행 상태 출력
            print(f"진행 중: {completed_items}/{total_items} 완료")

# 이벤트 루프를 통해 비동기 작업 실행
asyncio.run(process_places())

# 결과를 CSV 파일로 저장
save_file_path = r"C:\Users\ASUS ROG\Desktop\babsim_final.csv"
df.to_csv(save_file_path, index=False, encoding="utf-8-sig")
print("처리 완료! 저장되었습니다.")

#전체 처리시간 1시간 45분쯤 걸림