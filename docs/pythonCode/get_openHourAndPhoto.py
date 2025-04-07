
import pandas as pd
import requests


# 구글 API 키 설정
API_KEY = ""  # 실제 API 키로 변경Y"
SEARCH_URL = "https://places.googleapis.com/v1/places:searchText"

# CSV 파일 읽기
file_path = r"C:\Users\ASUS ROG\Desktop\babsim_with_coordinate.csv"
df = pd.read_csv(file_path, encoding="cp949")  # 이전에 cp949로 인코딩되었음(한글)

def search_place(name, city, district):
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
    response = requests.post(SEARCH_URL, json=data, headers=headers)
    return response.json()

# PlaceID, 위도, 경도, openingHours 관련 컬럼 추가 (사실 여기서 photoID도 같이 들고왔다면 따로 한 번더 받을 필요 없었는데데)
df["placeID"] = ""
df["위도"] = ""
df["경도"] = ""
df["openingHours"] = ""  # openingHours를 저장할 컬럼 추가
df["periods"] = ""       # periods를 저장할 컬럼 추가
df["weekdayDescriptions"] = ""  # weekdayDescriptions를 저장할 컬럼 추가

# 전체 항목 수와 처리 완료된 항목 수를 추적
total_items = len(df)
completed_items = 0

# 각 항목에 대해 장소 검색을 수행
for index, row in df.iterrows():
    name, city, district = row["업소명"], row["시도"], row["시군"]
    
    # 빈 값이 아니면 검색 시작
    if pd.notna(name) and pd.notna(city) and pd.notna(district):
        print(f"검색 중: {name} {city} {district}")
        
        # API 호출
        result = search_place(name, city, district)
        
        # 성공적인 결과 처리
        if "places" in result and len(result["places"]) > 0:
            place = result["places"][0]
            place_id = place.get("id", "")
            location = place.get("location", {})
            latitude = location.get("latitude", "")
            longitude = location.get("longitude", "")
            
            # openingHours 정보 추출
            opening_hours = place.get("regularOpeningHours", {})
            periods = opening_hours.get("periods", [])
            weekday_descriptions = opening_hours.get("weekdayDescriptions", [])
            
            # periods와 weekdayDescriptions을 문자열로 변환하여 저장
            periods_str = str(periods)
            weekday_descriptions_str = str(weekday_descriptions)
            
            # placeId, 위도, 경도, openingHours 추가
            df.at[index, "placeID"] = place_id
            df.at[index, "위도"] = latitude
            df.at[index, "경도"] = longitude
            df.at[index, "periods"] = periods_str
            df.at[index, "weekdayDescriptions"] = weekday_descriptions_str
            
            completed_items += 1  # 처리 완료된 항목 수 증가
            print(f"완료: {name}, {city}, {district} {place_id} - 위도: {latitude}, 경도: {longitude} {periods_str}")
        else:
            print(f"결과 없음: {name}, {city}, {district}")
    
    else:
        print(f"정보 부족: {name} {city} {district}")
    
    # 진행 상태 출력
    print(f"진행 중: {completed_items}/{total_items} 완료")

# 결과를 CSV 파일로 저장
save_file_path = r"C:\Users\ASUS ROG\Desktop\babsimmmmm.csv"
df.to_csv(save_file_path, index=False, encoding="utf-8-sig")
print("처리 완료! 저장되었습니다.")
