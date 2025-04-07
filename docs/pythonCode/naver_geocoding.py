import pandas as pd
import requests

# 네이버 API 키 설정
API_KEY_ID = ""
API_KEY = "" 

# 네이버 지오코딩 API 호출 함수
def get_coordinates(address):
    base_url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode"
    headers = {
        "x-ncp-apigw-api-key-id": API_KEY_ID,
        "x-ncp-apigw-api-key": API_KEY,
        "Accept": "application/json"
    }
    params = {"query": address}
    response = requests.get(base_url, headers=headers, params=params)

    if response.status_code == 200:
        result = response.json()
        if result['addresses']:
            lat = result['addresses'][0]['y']
            lon = result['addresses'][0]['x']
            print(f"Success! Latitude: {lat}, Longitude: {lon}")
            return lat, lon
        else:
            print("No address found.")
    else:
        print(f"Failed! Status code: {response.status_code}, Response: {response.text}")

    return None, None


# CSV 파일 읽기
file_path =  r"C:\Users\ASUS ROG\Desktop\babsimmmmm.csv" #이거는 본인 컴퓨터터 babsim파일 있는 주소
df = pd.read_csv(file_path, encoding="utf-8-sig")


# 좌표 추가
df["위도"], df["경도"] = zip(*df["주소"].apply(get_coordinates))

# 변환된 CSV 저장
save_file_path =  r"C:\Users\ASUS ROG\Desktop\real_final_bobsim.csv" #이거는 새로 좌표 추가된 파일 생성경로
df.to_csv(save_file_path, index=False, encoding="utf-8-sig")

print("좌표가 추가된 CSV 파일이 저장되었습니다.")


#총 4886데이터 약 10분소요 됐음