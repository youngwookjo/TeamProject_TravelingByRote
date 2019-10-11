package main;

import dto.Point;

public class Main {
	//두 좌표의 거리 구하기
	public static void main(String[] args) {
		Point pt1 = new Point(37.483327477565396, 127.02559779969133);//pt1에 mapx, mapy 좌표값을 저장
		double mapx1 = pt1.getMapx();//pt1의 mapx값을 mapx1에 저장
		double mapy1 = pt1.getMapy();//pt1의 mapy값을 mapy1에 저장
		System.out.println("첫번째 좌표값 : " + "(" + mapx1 +" , " + mapy1 + ")");
		
		Point pt2 = new Point(37.493853195638515, 127.01397673153947);//pt2에 mapx, mapy 좌표값을 저장
		double mapx2 = pt2.getMapx();//pt2의 mapx값을 mapx1에 저장
		double mapy2 = pt2.getMapy();//pt2의 mapy값을 mapy1에 저장
		System.out.println("두번째 좌표값 : " + "(" + mapx2 +" , " + mapy2 + ")");

		double distance = pt1.distance(pt2);
		
		System.out.println("두 좌표 사이의 거리:"+distance);
	}

}
