# 장소 이미지 가져오기
구글 place API를 통해 장소 사진 리스트(최대 3장) 받아온 후, s3 버킷에 저장해서 조회하는 방식으로 진행하였습니다.

# 비용적인 측면
- 총 4885개의 장소 정보가 있어서 디테일 정보 조회 (5000) + 사진 조회 (15000)의 구글 api 호출을 진행 -> 무료 크레딧 최대한 이용
- S3에 저장 후 관리 -> 조회 로직을 효율적으로 설계해서 조회 횟수 줄이기

```
# 📥 CSV 파일 읽기
try:
    df = pd.read_csv(input_file_path, encoding="utf-8")
    print(f"✅ CSV 파일 로드 완료: {input_file_path}")
except Exception as e:
    print(f"❌ CSV 파일 로드 실패: {e}")
    exit(1)

# 📷 사진 URL 가져오기 (최대 3장) - 개선된 버전
def get_photo_urls(place_id):
    try:
        details_url = f"https://maps.googleapis.com/maps/api/place/details/json?place_id={place_id}&fields=photos&key={API_KEY}"
        response = requests.get(details_url)

        if response.status_code == 200:
            result = response.json()
            if result.get("status") == "OK":
                photos = result.get("result", {}).get("photos", [])

                # 🛑 사진이 없는 경우 빈 리스트 반환
                if not photos:
                    print(f"⚠️ 사진 없음: {place_id}")
                    return []

                photo_urls = []
                for photo in photos[:3]:  # 최대 3장
                    photo_ref = photo.get("photo_reference")
                    if photo_ref:
                        photo_url = f"{PHOTO_URL}?maxwidth=1600&photoreference={photo_ref}&key={API_KEY}"
                        photo_urls.append(photo_url)

                return photo_urls
            else:
                print(f"⚠️ 사진 조회 실패: {result.get('status')}")
                return []
        else:
            print(f"❗ API 요청 오류: {response.status_code}")
            return []
    except Exception as e:
        print(f"🚨 사진 조회 예외 발생: {e}")
        return []

# 🪣 S3에 사진 업로드
def upload_to_s3(file_name, file_data):
    s3 = boto3.client(
        "s3",
        aws_access_key_id=AWS_ACCESS_KEY,
        aws_secret_access_key=AWS_SECRET_KEY,
    )
    try:
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=f"{S3_FOLDER}{file_name}",
            Body=file_data,
            ContentType="image/jpeg"
        )
        print(f"✅ S3 업로드 완료: {file_name}")
        return f"https://{BUCKET_NAME}.s3.amazonaws.com/{S3_FOLDER}{file_name}"
    except NoCredentialsError:
        print("❗ AWS 자격 증명 오류")
        return None
    except Exception as e:
        print(f"🚨 S3 업로드 예외 발생: {e}")
        return None

# 🚀 데이터 부분 처리 (151번째 행부터 500번째 행까지)
try:
    for index, row in df.iloc[150:500].iterrows():  # 151번째부터 500번째 행
        place_id = row.iloc[10]  # 장소 ID가 있는 11번째 열 (인덱스 10)

        # 장소 ID 유효성 검사
        if pd.notna(place_id):
            print(f"🔍 조회할 장소 ID (행 {index + 1}): {place_id}")

            # ✅ 사진 URL 가져오기 (사진이 있는 경우에만)
            photo_urls = get_photo_urls(place_id)

            s3_urls = []  # S3 URL 리스트

            # 사진 다운로드 및 S3 업로드
            for i, photo_url in enumerate(photo_urls):
                try:
                    photo_response = requests.get(photo_url)

                    if photo_response.status_code == 200:
                        file_name = f"{place_id}_{i+1}.jpg"
                        s3_url = upload_to_s3(file_name, photo_response.content)
                        if s3_url:
                            s3_urls.append(s3_url)
                    else:
                        print(f"❌ 사진 다운로드 실패: {photo_url}")
                except Exception as e:
                    print(f"🚨 사진 업로드 오류: {e}")

            # S3 URL을 CSV에 저장
            if s3_urls:
                formatted_urls = ", ".join(s3_urls)
                df.iat[index, 13] = formatted_urls  # 14번째 열(인덱스 13)에 저장
                print(f"✅ 완료: {place_id} - 사진 {len(s3_urls)}장")
            else:
                print(f"⚠️ 사진 없음 또는 저장 실패: {place_id}")

        else:
            print(f"❌ 장소 ID가 유효하지 않음 (행 {index + 1})")

    # 💾 결과 저장
    try:
        df.to_csv(output_file_path, index=False, encoding="utf-8-sig")
        print(f"✅ CSV 파일 저장 완료: {output_file_path}")
    except Exception as e:
        print(f"❌ CSV 파일 저장 실패: {e}")

except Exception as e:
    print(f"🚨 데이터 처리 중 오류 발생: {e}")

# 프로그램 종료
print("🚀 데이터 처리 완료 (151~500행)")


```

