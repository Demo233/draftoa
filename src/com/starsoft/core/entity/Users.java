package com.starsoft.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.starsoft.core.util.InitFieldAnnotation;
import com.starsoft.core.util.InitNameAnnotation;
import com.starsoft.core.util.StringUtil;

@Entity
@InitNameAnnotation("用户/帐号")
@Table(name="T_CORE_USER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Users extends BaseObject{
	@InitFieldAnnotation("登录密码")
	private String password;
	@InitFieldAnnotation("快速查询码")
	private String queryCode;
	@InitFieldAnnotation("所属部门")
	private String organId;
	@InitFieldAnnotation("排序号")
	private int sortCode;
	@InitFieldAnnotation("性别")
	private String sex;
	@InitFieldAnnotation("出生日期")
	private String birthday;
	@InitFieldAnnotation("工作岗位")
	private String duty;
	@InitFieldAnnotation("手机号码")
	private String mobilNumber;
	@InitFieldAnnotation("电子邮箱")
	private String email;
	@InitFieldAnnotation("QQ号码")
	private String qqNumber;
	@InitFieldAnnotation("注册时间")
	private Date createTime = new Date();
	@InitFieldAnnotation("个人的角色列表")
	private List<String> roles=new ArrayList<String>();
	@InitFieldAnnotation("个人的群组列表")
	private List<String> groups=new ArrayList<String>();
	@InitFieldAnnotation("个人头像")
	private String imageUrl;
	@InitFieldAnnotation("个性签名")
	private String remarker;
	@InitFieldAnnotation("输出日期")
	private String outDate;
	@InitFieldAnnotation("附议人是否附议")
	private String fymark;
	public Users(){
		this.id=StringUtil.generator();
	}
	@Column(length=32)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	@Column(length=4)
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	@Column(length=64)
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	@Column(length=64)
	public String getMobilNumber() {
		return mobilNumber;
	}
	public void setMobilNumber(String mobilNumber) {
		this.mobilNumber = mobilNumber;
	}
	@Column(length=128)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(length=16)
	public String getQqNumber() {
		return qqNumber;
	}
	public void setQqNumber(String qqNumber) {
		this.qqNumber = qqNumber;
	}
	@Transient
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	@Transient
	public List<String> getRoles() {
		return roles;
	}

	@Column(columnDefinition="int default 0")
	public int getSortCode() {
		return sortCode;
	}
	public void setSortCode(int sortCode) {
		this.sortCode = sortCode;
	}
	
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(length=32)
	public String getQueryCode() {
		return queryCode;
	}
	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getRemarker() {
		return remarker;
	}
	public void setRemarker(String remarker) {
		this.remarker = remarker;
	}
	public String getOrganId() {
		return organId;
	}
	public void setOrganId(String organId) {
		this.organId = organId;
	}
	public String getOutDate() {
		return outDate;
	}
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	@Transient
	public String getFymark() {
		return fymark;
	}
	public void setFymark(String fymark) {
		this.fymark = fymark;
	}
	
}