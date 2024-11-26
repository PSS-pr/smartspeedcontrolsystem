smartspeedcontrolsystem
-Andriod Studio (Java)
-Arduino
-Raspberry Pi
-FireBase
-Yolov5

위 프로젝트는 차량(RCcar) 를 어린이 보호구역내에서 한계속도를 제어하는 프로젝트입니다
차량(RcCar)는 2가지 조건의 의해 제어됩니다 
1.어린이 보호구역내 200M내 접근시
2.Detection 으로 어린이 보호구역 관련 객체 인식시

법적으로 어린이 보호구역은 학교나 시설 기준반경 200M입니다 따라서 200M 이내에 차량(RcCar) 속도를 50km 로 제한합니다
라즈베리파이로 어린이 보호구역 관련 객체를 인식합니다 표지판등을 인식하면 50KM로 차량(RcCar) 을 제어합니다
단 30KM 표지판을 인식하면 차량(RcCar) 최대속도를 30KM로 제어합니다

-

