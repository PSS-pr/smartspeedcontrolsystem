import cv2
from picamera2 import Picamera2
from ultralytics import YOLO
import time
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

picam2 = Picamera2()
picam2.preview_configuration.main.size = (640, 480)
picam2.preview_configuration.main.format = "RGB888"
picam2.preview_configuration.align()
picam2.configure("preview")
picam2.start()
cred = credentials.Certificate('smartspeedcontrolsystem-firebase-adminsdk-bbaop-0791636c73.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://smartspeedcontrolsystem-default-rtdb.firebaseio.com/'
})
dir_ref = db.reference()
model = YOLO("./Scc.pt")

frame_count = 0
start_time = time.time()

try:
    while True:
        try:
            frame = picam2.capture_array()
            results = model(frame)
            
            
            for result in results:
                for obj in result.boxes:
                    class_name = obj.name  # 객체 클래스명
                    print(f"Detected object: {class_name}") 
                    dir_ref.update({'YOLOVAL': '1ST'})
                    time.sleep(12)
                    dir_ref.update({'YOLOVAL': 'A'})

       	    
            # 이미지를 파일로 저장 (디스플레이 대신)
            annotated_frame = results[0].plot()
            cv2.imwrite(f"School/output_{frame_count}.jpg", annotated_frame)
            
            frame_count += 1
            if frame_count % 10 == 0:  # 10프레임마다 FPS 출력
                elapsed_time = time.time() - start_time
                fps = frame_count / elapsed_time
                print(f"FPS: {fps:.2f}")
            
            time.sleep(0.1)  # 처리 주기 조절 (필요에 따라 조정)
        
        except Exception as e:
            print(f"Error occurred: {e}")
            time.sleep(1)  # 오류 발생 시 잠시 대기

except KeyboardInterrupt:
    print("Program interrupted by user")

finally:
    picam2.stop()
    print("Program ended")
