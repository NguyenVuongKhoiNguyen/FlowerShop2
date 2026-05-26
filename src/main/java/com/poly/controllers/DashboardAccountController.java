package com.poly.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poly.models.requests.AccountRequest;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.AccountService;
import com.poly.utils.ImageUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("dashboard/account")
@RequiredArgsConstructor
public class DashboardAccountController {
	
	private final AccountService accountService;
	
	@PostMapping
	public AccountResponse create(@RequestParam("file") MultipartFile file, @RequestBody AccountRequest request) {
		
		String photo = ImageUtil.saveImage(file);
		request.setPhoto(photo);
		return accountService.create(request);
	}
	
	@PutMapping
	public AccountResponse update(@RequestParam("file") MultipartFile file, @RequestBody AccountRequest request) {
		
		if (file != null && !file.isEmpty()) {
			String photo = ImageUtil.saveImage(file);
			request.setPhoto(photo);
			ImageUtil.deleteImage(request.getPhoto());
		}
		return accountService.create(request);
	}
	
	@DeleteMapping("{username}")
	public void delete(@PathVariable String username) {
		accountService.delete(username);
	}
	
	@GetMapping
	public PageResponse<AccountResponse> filterAccounts(
			@RequestParam(defaultValue = "") String username,
			@RequestParam(defaultValue = "") String fullname,
			@RequestParam(defaultValue = "") String email,
			@RequestParam(defaultValue = "true") Boolean activated,
			@RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize
			) {
		
		return accountService.filterAndPaginateAccounts(username, fullname, email, activated, sortOrder, page, pageSize);
	}

	@GetMapping("/preload")
	public Map<String, PageResponse<AccountResponse>> preloadAccounts () {

		Map<String, PageResponse<AccountResponse>> map = new LinkedHashMap<>();
		
		for (int i = 0; i < 5; i++) {
			String key = ""  + "_" + "" + "_"  + "" + "_" + "true" + "_" + "DESC" + "_" + i + "_" + "5";
			map.put(key, accountService.filterAndPaginateAccounts(null, null, null, true, "DESC", i, 5));
		}

		return map;
	}

}
