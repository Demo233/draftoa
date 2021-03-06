package com.starsoft.oa.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.util.Streams;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.starsoft.cms.task.DownLoadUtil;
import com.starsoft.cms.task.UpLoadUtil;
import com.starsoft.core.controller.BaseAjaxController;
import com.starsoft.core.controller.BaseInterface;
import com.starsoft.core.entity.Users;
import com.starsoft.core.util.HttpUtil;
import com.starsoft.core.util.Page;
import com.starsoft.core.util.StringUtil;
import com.starsoft.core.vo.FileUpload;
import com.starsoft.oa.domain.ChengbanDomain;
import com.starsoft.oa.domain.FuyiDomain;
import com.starsoft.oa.domain.FuyiRecordDomain;
import com.starsoft.oa.domain.LianReturnDomain;
import com.starsoft.oa.domain.LianshenpiDomain;
import com.starsoft.oa.domain.LianshenpiRecordDomain;
import com.starsoft.oa.domain.MotionDomain;
import com.starsoft.oa.entity.Chengban;
import com.starsoft.oa.entity.ChengbanRecord;
import com.starsoft.oa.entity.LianReturn;
import com.starsoft.oa.entity.Lianshenpi;
import com.starsoft.oa.entity.LianshenpiRecord;
import com.starsoft.oa.entity.Motion;

/**
 * 
 * @Description 立案审批
 * @author 赵一好
 * @date 2016-11-16 上午9:01:07
 * 
 */
public class LianshenpiController extends MyBaseAjaxController {

	@Autowired
	private MotionDomain motionDomain;

	@Autowired
	private FuyiDomain fuyiDomain;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LianshenpiDomain lianshenpiDomain;

	@Autowired
	private LianshenpiRecordDomain lianshenpiRecordDomain;

	@Autowired
	private LianReturnDomain lianReturnDomain;
	
	@Autowired
	private ChengbanDomain chengbanDomain;

	// 查询状态为5的议案信息并,跳转到立案审批List页面
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = HttpUtil.convertModel(request);
		Page page = HttpUtil.convertPage(request);
		Users user = (Users) request.getSession().getAttribute("SESSONUSER");
		DetachedCriteria criteria = lianshenpiDomain.getCriteria(null);
		//criteria.add(Restrictions.eq("cbr", user.getId()));
		criteria.add(Restrictions.lt("read_index", "2"));
		List<Lianshenpi> lianshenpis = lianshenpiDomain.queryByCriteria(criteria,page);
		List<Motion> motions = new ArrayList<Motion>();
		for (Lianshenpi lianshenpi : lianshenpis) {
			String motionId = lianshenpi.getMotionId();
			Motion motion = (Motion) motionDomain.queryFirstByProperty("id",
					motionId);
			motions.add(motion);
		}
		// 查找附议人数
		for (Motion motion : motions) {
			// 查找附议总人数
			String fyrNum = fuyiDomain.findFuyiCount(motion.getId(), null) + "";
			motion.setFyrNum(fyrNum);
			
			// 查找附议赞同人数
			String agreeNum = fuyiDomain.findFuyiCount(motion.getId(), "1")+"";
			motion.setAgreeNum(agreeNum);
		}
		model.put("lists", motions);
		model.put("page", page);
		return new ModelAndView(this.getListPage(), model);
	}

	// 跳转到编辑页面
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String motionId = HttpUtil.getString(request, "id", "");
		HttpSession session = request.getSession();
		removeSesMotRec(session, motionId);
		Map<String, Object> model = HttpUtil.convertModel(request);
		// 根据页面传来的id查找对应的motion信息
		super.findMotionById(request, model);
		// 修改立案审批为已读状态
		Lianshenpi lianshenpi = (Lianshenpi) lianshenpiDomain
				.queryFirstByProperty("motionId", motionId);
		if(lianshenpi.getRead_index().equals("0")){
			lianshenpi.setRead_index("1");
		}
		lianshenpiDomain.update(lianshenpi);

		findMotRecs(motionId, model);
		
		// 生成一个立案号
		DetachedCriteria c = motionDomain.getCriteria(null);
		c.add(Restrictions.ne("lah", ""));
		c.addOrder(Property.forName("lah").desc());
		List<Motion> motions = motionDomain.queryByCriteria(c);
		String lah = "";
		if(motions.size()>0){
			lah = motions.get(0).getLah();
			String str = lah.substring(3, lah.length());
			lah = "LAH" + ((Long.parseLong(str) + 1)+"");
		}else{
			lah = "LAH" + StringUtil.generatorShort();
		}
		
		// 查询承办记录
		Chengban chengban = (Chengban) chengbanDomain.queryFirstByProperty("motionId", motionId);
		model.put("lah", lah);
		if(chengban!=null){
			model.put("chengban", chengban);
		}
		return new ModelAndView(this.getEditPage(), model);
	}

	// 保存立案人意见
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String gotourl = "/index.do";
		String motionId = HttpUtil.getString(request, "motionId", "");
		//Users user = (Users)request.getSession().getAttribute("SESSONUSER");
		String mark = HttpUtil.getString(request, "mark", "0");
		Lianshenpi lianshenpi = (Lianshenpi) this.baseDomain.queryFirstByProperty("motionId", motionId);
		this.bind(request, lianshenpi);
		this.saveBaseInfoToObject(request, lianshenpi);
		if (motionId == null || motionId.equals("")) {
			gotourl = "/404.htm";
			this.outFailString(request, response, "操作失败请重试", gotourl);
		}
		String lah = HttpUtil.getString(request, "lah");
		Motion motion = (Motion) motionDomain.query(motionId);
		motion.setLah(lah);
		motionDomain.update(motion);
		
		lianshenpiDomain.saveLianshenpiAndRec(lianshenpi,motionId,mark);
		/*
		this.baseDomain.update(lianshenpi);
		// 修改议案的状态为6
		if (motionId == null || motionId.equals("")) {
			gotourl = "/404.htm";
			this.outFailString(request, response, "操作失败请重试", gotourl);
		}
		Motion motion = (Motion) motionDomain.query(motionId);
		motion.setStatus((Integer.parseInt(motion.getStatus()) + 1) + "");
		motionDomain.update(motion);

		// 向立案审批回复表中插入一条数据

		LianReturn lianReturn = new LianReturn();
		lianReturn.setMotionId(motionId);
		lianReturn.setTime(new Date());
		lianReturn.setTname("立案回复");
		lianReturn.setRead_index("0");
		this.saveBaseInfoToObject(request, lianReturn);
		lianReturnDomain.save(lianReturn);*/
		//上传文件
		FileUpload entity=new FileUpload();
		bind(request, entity);
		String url=UpLoadUtil.myUpLoad(entity);
		if(!"".equals(url)){
			lianshenpi.setUrl(url);
		}
		this.baseDomain.update(lianshenpi);
		return new ModelAndView("redirect:/lianshenpi.do?action=list");
	}
	
	public void download(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String motionId = HttpUtil.getString(request, "motionId","");
		// 获取提案
		Lianshenpi lianshenpi = (Lianshenpi) this.baseDomain.queryFirstByProperty("motionId", motionId);
		String path = lianshenpi.getUrl();
		// 获取文件名称
        String[] split = path.split("\\\\");
        String fileName = split[split.length-1];
        
        DownLoadUtil.download(request, response, path, fileName);
        
	}
	
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return null;
	}

	public ModelAndView read(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return null;
	}

	public void update(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

	}

}
