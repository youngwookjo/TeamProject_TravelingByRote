
package main;
 
public class Mainother2 {
	//두 좌표의 거리를 mile, meter, kilo로 표현, lat1 lon1을 기준으로 가고 싶은 위치 lat2 lon2를 입력.
	public static void main(String[] args) {
		// 서초역 위도/경도, 교대역 위도/경도
        // 마일(Mile) 단위
        double distanceMile =
            distance(37.483327477565396, 127.02559779969133, 37.493853195638515, 127.01397673153947, "");
         
        // 미터(Meter) 단위
        double distanceMeter =
            distance(37.483327477565396, 127.02559779969133, 37.493853195638515, 127.01397673153947, "meter");
         
        // 킬로미터(Kilo Meter) 단위
        double distanceKiloMeter =
            distance(37.483327477565396, 127.02559779969133, 37.493853195638515, 127.01397673153947, "kilometer");
         
        System.out.println("distanceMile:"+distanceMile);
        System.out.println("distanceMeter:"+distanceMeter);
        System.out.println("distanceKiloMeter:"+distanceKiloMeter) ;
         
         
    }
     
    /**
     * 두 지점간의 거리 계산
     * @param lat1 지점 1 위도 
     * @param lon1 지점 1 경도 
     * @param lat2 지점 2 위도 
     * @param lon2 지점 2 경도 
     * @param unit 거리 표출단위
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
    	if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
         
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
         
        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }
 
        return (dist);
		}
    }
 
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
     
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
