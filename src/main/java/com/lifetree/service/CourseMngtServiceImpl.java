package com.lifetree.service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.directory.SearchResult;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.lifetree.entity.CourseDetails;
import com.lifetree.model.SearchInputs;
import com.lifetree.model.SearchResults;
import com.lifetree.repository.ICourseDetailsRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CourseMngtServiceImpl implements ICourseMngtService {

	@Autowired
	private ICourseDetailsRepository courseRepo;

	@Override
	public Set<String> showAllCourseCategories() {
		return courseRepo.getUniqueCourseCategories();
	}

	@Override
	public Set<String> showAllFaculties() {
		return courseRepo.getUniqueFacultyNames();
	}

	@Override
	public Set<String> showAllTrainingModes() {
		return courseRepo.getUniqueTrainingModes();
	}

	/*
	 * @Override public List<SearchResults> showCorsesByFilters(SearchInputs inputs)
	 * { // get all NonNull and non empty string values from inputs object and
	 * //prepare entity obj having non null data and also place in Example obj and
	 * pass it to findAll() CourseDetails entity= new CourseDetails();
	 * 
	 * String category=inputs.getCourseCategory(); if(category!=null &&
	 * !category.equals("") && category.length()!=0 )
	 * entity.setCourseCategory(category);
	 * 
	 * String facultyName=inputs.getFacultyName(); if(facultyName!=null &&
	 * !facultyName.equals("") && facultyName.length()!=0 )
	 * entity.setFacultyName(facultyName);
	 * 
	 * String trainingMode=inputs.getTrainingMode(); if(trainingMode!=null &&
	 * !trainingMode.equals("") && trainingMode.length()!=0 )
	 * entity.setTrainingMode(trainingMode);
	 * 
	 * LocalDateTime startDate=inputs.getStartsOn(); if(startDate!=null)
	 * entity.setStartDate(startDate);
	 * 
	 * Example<CourseDetails> example=Example.of(entity);
	 * 
	 * List<CourseDetails> listEntities = courseRepo.findAll(example);
	 * List<SearchResults> listResults= new ArrayList<>();
	 * listEntities.forEach(course->{ SearchResults result= new SearchResults();
	 * BeanUtils.copyProperties(course, result); listResults.add(result); });
	 * 
	 * return listResults; }
	 */

	@Override
	public List<SearchResults> showCoursesByFilters(SearchInputs inputs) {
		// get all NonNull and non empty string values from inputs object and
		// prepare entity obj having non null data and also place in Example obj and
		// pass it to findAll()
		CourseDetails entity = new CourseDetails();

		String category = inputs.getCourseCategory();
		if (StringUtils.hasLength(category))
			entity.setCourseCategory(category);

		String facultyName = inputs.getFacultyName();
		if (StringUtils.hasLength(facultyName))
			entity.setFacultyName(facultyName);

		String trainingMode = inputs.getTrainingMode();
		if (StringUtils.hasLength(trainingMode))
			entity.setTrainingMode(trainingMode);

		LocalDateTime startDate = inputs.getStartsOn();
		if (!ObjectUtils.isEmpty(startDate))
			entity.setStartDate(startDate);

		Example<CourseDetails> example = Example.of(entity);

		List<CourseDetails> listEntities = courseRepo.findAll(example);
		List<SearchResults> listResults = new ArrayList<>();
		listEntities.forEach(course -> {
			SearchResults result = new SearchResults();
			BeanUtils.copyProperties(course, result);
			listResults.add(result);
		});

		return listResults;
	}

	@Override
	public void generatePdfReport(SearchInputs inputs, HttpServletResponse res) throws Exception {

		List<SearchResults> listResults = showCoursesByFilters(inputs);
		preparePdf(res, listResults);
	}

	@Override
	public void generatePdfReportAll(HttpServletResponse res) throws Exception {

		// get all records form db table
		List<CourseDetails> list = courseRepo.findAll();
		// convert List<CourseDetails> to List<SearchResults>
		List<SearchResults> listResults = new ArrayList<>();
		list.forEach(course -> {
			SearchResults result = new SearchResults();
			BeanUtils.copyProperties(course, result);
			listResults.add(result);
		});
		preparePdf(res, listResults);

	}

	private void preparePdf(HttpServletResponse res, List<SearchResults> listResults) throws Exception {

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, res.getOutputStream());
		document.open();

		Font font = FontFactory.getFont(FontFactory.COURIER);
		font.setSize(30);
		font.setColor(Color.RED);

		Paragraph para = new Paragraph("Search report of courses", font);
		para.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(para);

		PdfPTable table = new PdfPTable(10);
		table.setWidthPercentage(70);
		table.setWidths(new float[] { 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f });
		table.setSpacingBefore(2.0f);

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.gray);
		cell.setPadding(5);

		Font cellfont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		cellfont.setColor(Color.BLACK);

		cell.setPhrase(new Phrase("CourseID", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("CourseName", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Course Category", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Faculty Name", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Location", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Fee", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Course status", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Training mode", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Admin contact", cellfont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("start on", cellfont));
		table.addCell(cell);

		listResults.forEach(result -> {
			table.addCell(String.valueOf(result.getCourseId()));
			table.addCell(result.getCourseName());
			table.addCell(result.getCourseCategory());
			table.addCell(result.getFacultyName());
			table.addCell(result.getLocation());
			table.addCell(String.valueOf(result.getFee()));
			table.addCell(result.getCourseStatus());
			table.addCell(result.getTrainingMode());
			table.addCell(String.valueOf(result.getAdminContact()));
			table.addCell(result.getStartDate().toString());
		});
		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelReport(SearchInputs inputs, HttpServletResponse res) throws Exception {
		// in this method we add everything to workbook sheet(of workbook)
		// and also writing workbook data to response object using outputstream that is
		// pointing to response object is another thing
		List<SearchResults> listResults = showCoursesByFilters(inputs);
		prepareExcelFile(listResults, res);

	}

	@Override
	public void generateExcelReportAll(HttpServletResponse res) throws Exception {
		// get all records form db table
		List<CourseDetails> list = courseRepo.findAll();
		// convert List<CourseDetails> to List<SearchResults>
		List<SearchResults> listResults = new ArrayList<>();
		list.forEach(course -> {
			SearchResults result = new SearchResults();
			BeanUtils.copyProperties(course, result);
			listResults.add(result);
		});
		prepareExcelFile(listResults, res);

	}

	private void prepareExcelFile(List<SearchResults> listResults, HttpServletResponse res) throws Exception {
		// create Excel workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// create sheet in workbook
		HSSFSheet sheet1 = workbook.createSheet("CourseDetails");
		// create heading row in sheet1
		HSSFRow headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("CourseId");
		headerRow.createCell(1).setCellValue("CourseName");
		headerRow.createCell(2).setCellValue("Location");
		headerRow.createCell(3).setCellValue("CourseCategory");
		headerRow.createCell(4).setCellValue("FacultyName");
		headerRow.createCell(5).setCellValue("Fee");
		headerRow.createCell(6).setCellValue("AdminContact");
		headerRow.createCell(7).setCellValue("ModeOfTraining");
		headerRow.createCell(8).setCellValue("StartDate");
		headerRow.createCell(9).setCellValue("CourseStatus");
		int i = 1;
		for (SearchResults results : listResults) {
			HSSFRow dataRow = sheet1.createRow(i);
			dataRow.createCell(0).setCellValue(results.getCourseId());
			dataRow.createCell(1).setCellValue(results.getCourseName());
			dataRow.createCell(2).setCellValue(results.getLocation());
			dataRow.createCell(3).setCellValue(results.getCourseCategory());
			dataRow.createCell(4).setCellValue(results.getFacultyName());
			dataRow.createCell(5).setCellValue(results.getFee());
			dataRow.createCell(6).setCellValue(results.getAdminContact());
			dataRow.createCell(7).setCellValue(results.getTrainingMode());
			dataRow.createCell(8).setCellValue(results.getStartDate());
			dataRow.createCell(9).setCellValue(results.getCourseStatus());
			i++;
		}
		// get output stream pointing to response object
		ServletOutputStream outputStream = res.getOutputStream();
		// write the excel workbook results to response object using above stream
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}

}
