package com.hk.entity.query;

import com.hk.entity.enums.PageSize;

public class SimplePage {
    private int pageNo;
    private Integer countTotal;
    private Integer pageSize;
    private int pageTotal;
    private int start;
    private int end;

    public SimplePage(Integer pageNo, Integer countTotal, Integer pageSize) {
        if (null == pageNo) pageNo = 0;
        this.pageNo = pageNo;
        this.countTotal = countTotal;
        this.pageSize = pageSize;
        action();
    }

    public SimplePage(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(Integer countTotal) {
        this.countTotal = countTotal;
        this.action();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private void action() {
        if (this.pageSize <= 0) this.pageSize=PageSize.SIZE20.getSize();
        if (this.countTotal > 0) this.pageTotal = this.countTotal % this.pageSize == 0 ? this.countTotal / this.pageSize : this.countTotal / this.pageSize + 1;
        else pageTotal = 1;
        if (pageNo <= 1)pageNo = 1;
        if (pageNo > pageTotal)pageNo = pageTotal;
        this.start = (pageNo - 1) * pageSize;
        this.end = this.pageSize;
    }

}
