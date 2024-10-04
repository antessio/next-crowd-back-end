package nextcrowd.crowdfunding.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CrowdfundingProjectOwner.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CrowdfundingProjectOwner_ {

	public static final String PROJECTS = "projects";
	public static final String IMAGE_URL = "imageUrl";
	public static final String NAME = "name";
	public static final String ID = "id";

	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner#projects
	 **/
	public static volatile SetAttribute<CrowdfundingProjectOwner, CrowdfundingProject> projects;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner#imageUrl
	 **/
	public static volatile SingularAttribute<CrowdfundingProjectOwner, String> imageUrl;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner#name
	 **/
	public static volatile SingularAttribute<CrowdfundingProjectOwner, String> name;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner#id
	 **/
	public static volatile SingularAttribute<CrowdfundingProjectOwner, Long> id;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner
	 **/
	public static volatile EntityType<CrowdfundingProjectOwner> class_;

}

