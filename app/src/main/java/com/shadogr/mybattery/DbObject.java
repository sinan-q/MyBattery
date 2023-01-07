package com.shadogr.mybattery;

public class DbObject {
    private int usageCurrentCount;
    private int logId;
    private long time;
    private int batLvl;
    private int temp;
    private int current;
    private int sot;
    private int scrOffCurr;
    private int scrOnCurr;
    private String usageId;
    private String usageName;
    private int usageCurrent;
    private Boolean screenState;
    private int romId;
    private int romTime;
    private String romKernel;
    private String romName;
    private int romCount;
    private String romProp;

    public DbObject(){}
    public DbObject(int logId, long time, int batLvl, int temp, int current, int sot, int scrOffCurr, int scrOnCurr ,boolean screenState){
        this.logId = logId;
        this.time = time;
        this.batLvl = batLvl;
        this.temp = temp;
        this.current = current;
        this.sot = sot;
        this.scrOffCurr = scrOffCurr;
        this.scrOnCurr = scrOnCurr;
        this.screenState =screenState;
    }
    public DbObject(int logId1, long time1){
        this.logId = logId1;
        this.time = time1;
        this.batLvl = -1;
        this.temp = 0;
        this.current = 0;
        this.sot = 0;
        this.scrOffCurr = 0;
        this.scrOnCurr = 0;
        this.screenState =Boolean.FALSE;
    }
    public DbObject(String usageId,String usageName, int usageCurrent,int usageCurrentCount) {
        this.usageId = usageId;
        this.usageName = usageName;
        this.usageCurrent = usageCurrent;
        this.usageCurrentCount = usageCurrentCount;
    }

    public DbObject(int romId, int romTime, String romKernel, String romName, int romCount,String romProp) {

        this.romId = romId;
        this.romTime = romTime;
        this.romKernel = romKernel;
        this.romName = romName;
        this.romCount = romCount;
        this.romProp = romProp;
    }



    public String getRomProp() {
        return romProp;
    }

    public void setRomProp(String romProp) {
        this.romProp = romProp;
    }

    public String getUsageId() {
        return usageId;
    }
    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    public String getUsageName() {
        return usageName;
    }
    public void setUsageName(String usageName) {
        this.usageName = usageName;
    }

    public int getUsageCurrent() {
        return usageCurrent;
    }
    public void setUsageCurrent(int usageCurrent) {
        this.usageCurrent = usageCurrent;
    }

    public int getUsageCurrentCount() {
        return usageCurrentCount;
    }
    public void setUsageCurrentCount(int usageCurrentCount) {
        this.usageCurrentCount = usageCurrentCount;
    }

    public int getlogId(){
        return this.logId;
    }
    public void setLogId(int id){
        this.logId = id;
    }

    public long getTime(){
        return this.time;
    }
    public void setTime(long time){
        this.time = time;
    }

    public int getBatLvl(){
        return this.batLvl;
    }
    public void setBatLvl(int batLvl){
        this.batLvl = batLvl;
    }

    public int getTemp(){
        return this.temp;
    }
    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getCurrent() {
        return this.current;
    }
    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSot() {
        return this.sot;
    }
    public void setSot(int sot) {
        this.sot = sot;
    }

    public int getScrOffCurr() {
        return scrOffCurr;
    }
    public void setScrOffCurr(int scrOffCurr) {
        this.scrOffCurr = scrOffCurr;
    }

    public int getScrOnCurr() {
        return scrOnCurr;
    }
    public void setScrOnCurr(int scrOnCurr) {
        this.scrOnCurr = scrOnCurr;
    }

    public Boolean getScreenState() {
        return screenState;
    }
    public void setScreenState(Boolean screenState) {
        this.screenState = screenState;
    }


    public int getRomId() {
        return romId;
    }

    public void setRomId(int romId) {
        this.romId = romId;
    }

    public int getRomTime() {
        return romTime;
    }

    public void setRomTime(int romTime) {
        this.romTime = romTime;
    }

    public String getRomKernel() {
        return romKernel;
    }

    public void setRomKernel(String romKernel) {
        this.romKernel = romKernel;
    }

    public String getRomName() {
        return romName;
    }

    public void setRomName(String romName) {
        this.romName = romName;
    }

    public int getRomCount() {
        return romCount;
    }

    public void setRomCount(int romCount) {
        this.romCount = romCount;
    }
}
