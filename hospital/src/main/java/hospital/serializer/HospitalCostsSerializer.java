package main.java.hospital.serializer;

import com.owlike.genson.Genson;
import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;

public class HospitalCostsSerializer {
    private Genson genson;

    public HospitalCostsSerializer(){
        genson = new Genson();
    }

    public String requestToString(HospitalCostsRequest request){
        return genson.serialize(request);
    }

    public HospitalCostsRequest requestFromString(String str){
        return  genson.deserialize(str, HospitalCostsRequest.class);
    }

    public String replyToString(HospitalCostsReply reply){
        return genson.serialize(reply);
    }

    public HospitalCostsReply replyFromString(String str){
        return genson.deserialize(str, HospitalCostsReply.class);
    }
}
