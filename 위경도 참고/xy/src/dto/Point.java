package dto;

import lombok.Data;

@Data
public class Point {
	private double mapx;
	private double mapy;
	
	public Point() {}
	
	public Point(double mapx, double mapy) {
		this.mapx=mapx;
		this.mapy=mapy;
	}
	
	public double distance(Point pt) {
		return Math.sqrt((pt.mapx-mapx)*(pt.mapx-mapx)+(pt.mapy-mapy)*(pt.mapy-mapy));
	}
//	Math.sqrt : 함수 안에 값을 루트로 계산
//	pt.mapx, pt.apx : 외부에서 받아오는 좌표값
//	mapx, mapy : 기존에 저장되어 있던 좌표값
		
}
