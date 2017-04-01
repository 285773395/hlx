package com.zslin.basic.model;

import javax.persistence.*;

/**
 * 系统配置
 * @author zslin.com 20160519
 *
 */
@Entity
@Table(name="a_app_config")
public class AppConfig {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	/** 系统名称 */
	@Column(name="app_name")
	private String appName;
	
	/** 当前版本 */
	@Column(name="app_version")
	private String appVersion;

	/** 创建日期 */
	@Column(name="create_date")
	private String createDate;
	
	/** 初始化标记，如果为空或为0，表示都可以初始化 */
	@Column(name="init_flag")
	private String initFlag;
	
	/** 首页路径 */
	@Column(name="index_page")
	private String indexPage;
	
	/** 页末联系人 */
	private String contant;

	/** 管理员邮箱 */
	@Column(name = "admin_email")
	private String adminEmail;

	/** 微信支付，1-开启；0-未开启 */
	@Column(name = "wx_pay")
	private String wxPay;

	/** 联系地址 */
	private String address;

	/** 联系电话 */
	private String phone;

	/** 纬度 */
	private String latitude;

	/** 经度 */
	private String longitude;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWxPay() {
		return wxPay;
	}

	public void setWxPay(String wxPay) {
		this.wxPay = wxPay;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getContant() {
		return contant;
	}

	public void setContant(String contant) {
		this.contant = contant;
	}

	/** 首页路径 */
	public String getIndexPage() {
		return indexPage;
	}

	/** 首页路径 */
	public void setIndexPage(String indexPage) {
		this.indexPage = indexPage;
	}

	/** 初始化标记，如果为空或为0，表示都可以初始化 */
	public String getInitFlag() {
		return initFlag;
	}

	/** 初始化标记，如果为空或为0，表示都可以初始化 */
	public void setInitFlag(String initFlag) {
		this.initFlag = initFlag;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
}
