package com.lifetree.service;

import java.util.List;
import java.util.Set;

import com.lifetree.model.SearchInputs;
import com.lifetree.model.SearchResults;

import jakarta.servlet.http.HttpServletResponse;

public interface ICourseMngtService {

	public Set<String> showAllCourseCategories();
	public Set<String> showAllFaculties();
	public Set<String> showAllTrainingModes();
	
	public List<SearchResults> showCoursesByFilters(SearchInputs inputs);
	// why HttpServletRespose :---response is to set the header content , which tells to browser to not to display make it downloadable.
	public void generatePdfReport(SearchInputs inputs,HttpServletResponse res)throws Exception;
	public void generateExcelReport(SearchInputs inputs,HttpServletResponse res)throws Exception;
	
	public void generatePdfReportAll(HttpServletResponse res)throws Exception;
	
	public void generateExcelReportAll(HttpServletResponse res)throws Exception;
}
