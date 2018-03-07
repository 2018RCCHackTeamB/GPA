package ritsumeikancomputerclub.gpa;

import io.realm.RealmObject;

public class SpotModel  extends RealmObject{

    private int uuId;  //ユニークID
    private int prefectureId;   // 県ID
    private int transportId; // 乗り物ID
    private String name; // 駅名
    private float latitude;  // 緯度
    private float longitude;  // 経度
    private String updatedAt; //更新された日時

    public int getUuId(){return uuId;}
    public void setUuId(int uuId) { this.uuId = uuId; }

    public int getPrefectureId(){return prefectureId;}
    public void setPrefectureId(int prefectureId) { this.prefectureId = prefectureId ;}

    public int getTransportId(){return transportId;}
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getLatitude() { return latitude; }
    public void setLatitude(float latitude) { this.latitude = latitude; }

    public float getLongitude() { return longitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

}

