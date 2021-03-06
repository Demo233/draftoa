package com.starsoft.cms.domainImpl;

import org.springframework.stereotype.Service;

import com.starsoft.core.domainImpl.BaseDomainImpl;
import org.springframework.transaction.annotation.Transactional;
import com.starsoft.cms.domain.OnlineSurveyDomain;
@Service("onlineSurveyDomain")
@Transactional
public class OnlineSurveyDomainImpl extends BaseDomainImpl implements OnlineSurveyDomain{
	public OnlineSurveyDomainImpl(){
		this.setClassName("com.starsoft.cms.entity.OnlineSurvey");
	}
	
}
