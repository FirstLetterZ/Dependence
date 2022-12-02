package com.zpf.aaa;

import java.util.List;

public class ProvinceBean extends AddressBean {
    private List<CityBean> children;

    public ProvinceBean() {
    }

    public List<ProvinceBean.CityBean> getChildren() {
        return this.children;
    }

    public void setChildren(List<ProvinceBean.CityBean> children) {
        this.children = children;
    }

    public class CityBean extends AddressBean {
        private List<ProvinceBean.CityBean.AreaBean> children;

        public CityBean() {
        }

        public List<ProvinceBean.CityBean.AreaBean> getChildren() {
            return this.children;
        }

        public void setChildren(List<ProvinceBean.CityBean.AreaBean> children) {
            this.children = children;
        }

        public class AreaBean extends AddressBean {
            public AreaBean() {
            }
        }
    }
}
