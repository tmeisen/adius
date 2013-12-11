package org.adiusframework.service;

import org.adiusframework.service.xml.Category;
import org.adiusframework.service.xml.ServiceCapability;

public class CategoryServiceCapabilityRule implements ServiceCapabilityRule {

	private static final long serialVersionUID = 9178039968195308469L;

	private Category category;

	private String subcategory;

	public CategoryServiceCapabilityRule() {
		// nothing to do
	}

	public CategoryServiceCapabilityRule(Category category) {
		setCategory(category);
		setSubcategory(null);
	}

	public CategoryServiceCapabilityRule(Category category, String subcategory) {
		this(category);
		setSubcategory(subcategory);
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	@Override
	public boolean satisfiedBy(ServiceCapability capability) {

		// check category match
		if (capability.getCategory().equals(getCategory())) {

			// check subcategory match
			if ((getSubcategory() == null && capability.getSubcategory() == null)
					|| (getSubcategory() != null && getSubcategory().equals(capability.getSubcategory())))
				return true;
		}
		return false;
	}

}
