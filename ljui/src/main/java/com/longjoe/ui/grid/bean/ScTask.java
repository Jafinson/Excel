package com.longjoe.ui.grid.bean;

import java.io.Serializable;

/**
 * Created by Heycz on 2015/4/28.
 */
public class ScTask extends LjBean implements Serializable {
    private String relCode;
    private String ml_mtrlname;
    private String ml_mtrlmode;
    private String sonMtrlName;
    private String mtrlMode;
    private String proName;
    private double proqty;
    private double unArrangeQty;
    private String rqDate;

    public String getMl_mtrlmode() {
        return ml_mtrlmode;
    }

    public void setMl_mtrlmode(String ml_mtrlmode) {
        this.ml_mtrlmode = ml_mtrlmode;
    }

    public String getMl_mtrlname() {
        return ml_mtrlname;
    }

    public void setMl_mtrlname(String ml_mtrlname) {
        this.ml_mtrlname = ml_mtrlname;
    }

    public String getMtrlMode() {
        return mtrlMode;
    }

    public void setMtrlMode(String mtrlMode) {
        this.mtrlMode = mtrlMode;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public double getProqty() {
        return proqty;
    }

    public void setProqty(double proqty) {
        this.proqty = proqty;
    }

    public String getRelCode() {
        return relCode;
    }

    public void setRelCode(String relCode) {
        this.relCode = relCode;
    }

    public String getRqDate() {
        return rqDate;
    }

    public void setRqDate(String rqDate) {
        this.rqDate = rqDate;
    }

    public String getSonMtrlName() {
        return sonMtrlName;
    }

    public void setSonMtrlName(String sonMtrlName) {
        this.sonMtrlName = sonMtrlName;
    }

    public double getUnArrangeQty() {
        return unArrangeQty;
    }

    public void setUnArrangeQty(double unArrangeQty) {
        this.unArrangeQty = unArrangeQty;
    }
}
