package com.lifetree.ms;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifetree.entity.CourseDetails;
import com.lifetree.model.SearchInputs;
import com.lifetree.model.SearchResults;
import com.lifetree.service.ICourseMngtService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/reporting/api")
@OpenAPIDefinition(info = @Info(title = "Reporting API", version = "1.0", description = "Reporting API supporting File Download Operations", license = @License(name = "Naresh IT", url = "http://nareshit.com"), contact = @Contact(url = "http://gigantic-server.com", name = "ramesh", email = "Ramesh@gigagantic-server.com")))
public class CoursesReportOperationsController {

	@Autowired
	private ICourseMngtService courseService;

	@Operation(summary = "Get Unique courses information", responses = {
			@ApiResponse(description = "Courses info ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetails.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@GetMapping("/courses")
	public ResponseEntity<?> fetchCourseCategories() {
		try {
			Set<String> coursesInfo = courseService.showAllCourseCategories();
			return new ResponseEntity<Set<String>>(coursesInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Get Unique faculties information", responses = {
			@ApiResponse(description = "Faculties info ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetails.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@GetMapping("/faculties")
	public ResponseEntity<?> fetchAllFaculties() {
		try {
			Set<String> facultyInfo = courseService.showAllFaculties();
			return new ResponseEntity<Set<String>>(facultyInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Operation(summary = "Get  Unique Training modes information", responses = {
			@ApiResponse(description = "Training modes info ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetails.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@GetMapping("/trainingModes")
	public ResponseEntity<?> fetchTrainingModes() {
		try {
			Set<String> trainingModeInfo = courseService.showAllTrainingModes();
			return new ResponseEntity<Set<String>>(trainingModeInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Operation(summary = "Get Coouses Info By Adding filters", responses = {
			@ApiResponse(description = "Training modes info ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResults.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@PostMapping("/search")
	public ResponseEntity<?> fetchCoursesByFilters(@RequestBody SearchInputs inputs) {
		try {
			List<SearchResults> list = courseService.showCoursesByFilters(inputs);
			return new ResponseEntity<List<SearchResults>>(list, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Operation(summary = "Download pdf file with filters", responses = {
			@ApiResponse(description = "pdf file info ", content = @Content(mediaType = "application/pdf", schema = @Schema(implementation = SearchInputs.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@PostMapping("/pdfDownload")
	public void generatePdfReport(@RequestBody SearchInputs inputs, HttpServletResponse res) {
		try {
			// set the repsonse content type
			res.setContentType("application/pdf");
			// set the content disposition header to response content going to browser as
			// downloadable file.
			res.setHeader("Content-Disposition", "attachment;fileName=courses.pdf");
			// use service.
			courseService.generatePdfReport(inputs, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Operation(summary = "Download excel file with filters", responses = {
			@ApiResponse(description = "excel file info ", content = @Content(mediaType = "application/vnd.ms-excel", schema = @Schema(implementation = SearchInputs.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@PostMapping("/excelDownload")
	public void generateExcelReport(@RequestBody SearchInputs inputs, HttpServletResponse res) {
		try {
			// set the repsonse content type
			res.setContentType("application/vnd.ms-excel");
			// set the content disposition header to response content going to browser as
			// downloadable file.
			res.setHeader("Content-Disposition", "attachment;fileName=courses.xls");
			// use service.
			courseService.generateExcelReport(inputs, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Operation(summary = "Download pdf file", responses = {
			@ApiResponse(description = "pdf file info without filters ", content = @Content(mediaType = "application/pdf", schema = @Schema(implementation = SearchResults.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@GetMapping("/pdfDownload-all")
	public void generatePdfReportAll(HttpServletResponse res) {
		try {
			// set the repsonse content type
			res.setContentType("application/pdf");
			// set the content disposition header to response content going to browser as
			// downloadable file.
			res.setHeader("Content-Disposition", "attachment;fileName=courses.pdf");
			// use service.
			courseService.generatePdfReportAll(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Operation(summary = "Download excel file", responses = {
			@ApiResponse(description = "excel file info without filters", content = @Content(mediaType = "application/vnd.ms-excel", schema = @Schema(implementation = SearchResults.class))),
			@ApiResponse(responseCode = "500", description = "Wrong url") })
	@GetMapping("/excelDownload-all")
	public void generateExcelReportAll(HttpServletResponse res) {
		try {
			// set the repsonse content type
			res.setContentType("application/vnd.ms-excel");
			// set the content disposition header to response content going to browser as
			// downloadable file.
			res.setHeader("Content-Disposition", "attachment;fileName=courses.xls");
			// use service.
			courseService.generateExcelReportAll(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
