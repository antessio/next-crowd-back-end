package nextcrowd.crowdfunding.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

@StaticMetamodel(CrowdfundingProject.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CrowdfundingProject_ {

	public static final String SUMMARY = "summary";
	public static final String OWNER = "owner";
	public static final String COLLECTED_AMOUNT = "collectedAmount";
	public static final String NUMBER_OF_BACKERS = "numberOfBackers";
	public static final String PROJECT_END_DATE = "projectEndDate";
	public static final String DESCRIPTION = "description";
	public static final String TITLE = "title";
	public static final String MINIMUM_INVESTMENT = "minimumInvestment";
	public static final String PROJECT_VIDEO_URL = "projectVideoUrl";
	public static final String IMAGE_URL = "imageUrl";
	public static final String PROJECT_START_DATE = "projectStartDate";
	public static final String EXPECTED_PROFIT = "expectedProfit";
	public static final String REQUESTED_AMOUNT = "requestedAmount";
	public static final String CURRENCY = "currency";
	public static final String RISK = "risk";
	public static final String ID = "id";
	public static final String REWARDS = "rewards";

	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#summary
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> summary;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#owner
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, CrowdfundingProjectOwner> owner;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#collectedAmount
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, BigDecimal> collectedAmount;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#numberOfBackers
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, Integer> numberOfBackers;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#projectEndDate
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, Instant> projectEndDate;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#description
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> description;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#title
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> title;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#minimumInvestment
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, BigDecimal> minimumInvestment;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#projectVideoUrl
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> projectVideoUrl;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#imageUrl
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> imageUrl;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#projectStartDate
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, Instant> projectStartDate;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#expectedProfit
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, BigDecimal> expectedProfit;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#requestedAmount
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, BigDecimal> requestedAmount;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#currency
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, String> currency;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#risk
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, Integer> risk;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#id
	 **/
	public static volatile SingularAttribute<CrowdfundingProject, Long> id;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject
	 **/
	public static volatile EntityType<CrowdfundingProject> class_;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProject#rewards
	 **/
	public static volatile SetAttribute<CrowdfundingProject, ProjectReward> rewards;

}

