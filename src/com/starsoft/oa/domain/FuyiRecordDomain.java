package com.starsoft.oa.domain;

import com.starsoft.core.domain.BaseDomain;
import com.starsoft.oa.entity.Fuyi;
import com.starsoft.oa.entity.FuyiRecord;
import com.starsoft.oa.entity.Motion;

/**
 * 
 * @Description 附议记录
 * @author 赵一好
 * @date 2016-11-10 下午2:01:00
 * 
 */

public interface FuyiRecordDomain extends BaseDomain {

	// 向附议记录表中插入一条数据
	public void saveFyRecAndUpdateMot(FuyiRecord fyRec,Motion motion,String createId) throws Exception ;


	
}
