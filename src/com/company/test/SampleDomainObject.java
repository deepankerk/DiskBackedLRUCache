package com.company.test;

import java.io.Serializable;
import java.util.Date;

public class SampleDomainObject implements Serializable {
    private Integer id1;
    private Date id2;
    private String val;

    public SampleDomainObject(Integer id1, Date id2, String val) {
        this.id1 = id1;
        this.id2 = id2;
        this.val = val;
    }

    public Integer getId1() {
        return id1;
    }

    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    public Date getId2() {
        return id2;
    }

    public void setId2(Date id2) {
        this.id2 = id2;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SampleDomainObject)) return false;

        SampleDomainObject that = (SampleDomainObject) o;

        if (!getId1().equals(that.getId1())) return false;
        if (!getId2().equals(that.getId2())) return false;
        return getVal().equals(that.getVal());
    }

    @Override
    public int hashCode() {
        int result = getId1().hashCode();
        result = 31 * result + getId2().hashCode();
        result = 31 * result + getVal().hashCode();
        return result;
    }
}
