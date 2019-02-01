package in.abongcher.tbec;

/**
 * Created by abongcher on 17/5/17.
 */

public class Contributor {

    public byte[] image;

    public String name;
    public String gender;
    public String mobileNo;
    public int amount;

    public Contributor(){}

    public Contributor(int amount){
        this.amount = amount;
    }

    public Contributor(byte[] image, String name, String gender, String mobileNo, int amount){
        this.image = image;
        this.name = name;
        this.gender = gender;
        this.mobileNo = mobileNo;
        this.amount = amount;
    }


    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return this.gender;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }


}
