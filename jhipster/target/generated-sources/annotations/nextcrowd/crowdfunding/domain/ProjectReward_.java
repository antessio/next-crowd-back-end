package nextcrowd.crowdfunding.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ProjectReward.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class ProjectReward_ {

	public static final String CROWDFUNDING_PROJECT = "crowdfundingProject";
	public static final String IMAGE_URL = "imageUrl";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";

	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward#crowdfundingProject
	 **/
	public static volatile SingularAttribute<ProjectReward, CrowdfundingProject> crowdfundingProject;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward#imageUrl
	 **/
	public static volatile SingularAttribute<ProjectReward, String> imageUrl;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward#name
	 **/
	public static volatile SingularAttribute<ProjectReward, String> name;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward#description
	 **/
	public static volatile SingularAttribute<ProjectReward, String> description;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward#id
	 **/
	public static volatile SingularAttribute<ProjectReward, Long> id;
	
	/**
	 * @see nextcrowd.crowdfunding.domain.ProjectReward
	 **/
	public static volatile EntityType<ProjectReward> class_;

}

