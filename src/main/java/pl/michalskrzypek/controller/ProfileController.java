package pl.michalskrzypek.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import pl.michalskrzypek.dao.AccountDAO;
import pl.michalskrzypek.dao.AddressDAO;
import pl.michalskrzypek.dao.OrderDetailDAO;
import pl.michalskrzypek.entity.Account;
import pl.michalskrzypek.entity.Address;
import pl.michalskrzypek.entity.OrderDetail;
import pl.michalskrzypek.entity.Product;
import pl.michalskrzypek.model.AccountModel;
import pl.michalskrzypek.utility.FileUploadUtility;


/**
 * 
 * @author Michal SKrzypek
 *Controller responsible for displaying proper data in a profile page
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	HttpSession session;

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	AddressDAO addressDAO;
	
	@Autowired
	OrderDetailDAO orderDetailDAO;

	@RequestMapping("/show")
	public ModelAndView showProfile(@RequestParam(name = "success", required = false) String success,
			@RequestParam(name = "error", required = false) String error) {
		ModelAndView mv = new ModelAndView("home");

		if (success != null) {
			if (success.equalsIgnoreCase("billing_address")) {
				mv.addObject("message", "Billing address has been added successfully!");
			} else if (success.equalsIgnoreCase("updating_billing_address")) {
				mv.addObject("message", "Billing address has been updated successfully!");
			} else if (success.equalsIgnoreCase("shipping_address")) {
				mv.addObject("message", "Shipping address has been added successfully!");
			}

		}
		if (error != null) {
			if (error.equals("photo_upload")) {
				mv.addObject("message", "Couldn't upload a photo...");
			} else if (error.equals("updating_billing_address")) {
				mv.addObject("message", "Couldn't update billing address...");
			}
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountDAO.get(auth.getName());
		Address billingAddress = addressDAO.getBillingAddress(account.getId());

		if (billingAddress != null) {
			mv.addObject("billingAddress", billingAddress);
		}

		List<Address> shippingAddresses = addressDAO.getShippingAddresses(account.getId());
		if (shippingAddresses != null && shippingAddresses.size() > 0) {
			mv.addObject("shippingAddresses", shippingAddresses);
		}

		mv.addObject("title", "Profile");
		mv.addObject("userClickedProfile", true);
		mv.addObject("account", account);

		return mv;
	}
	
	@RequestMapping("/show/orders")
	public ModelAndView showOrders() {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("title", "Your orders");
		mv.addObject("userClickedShowOrders", true);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountDAO.get(auth.getName());
		
		List<OrderDetail> orders = orderDetailDAO.listAllOrders(account.getId());
		if(orders != null) {
			mv.addObject("orders", orders);
			mv.addObject("orderDAO", orderDetailDAO);
		}
		
		return mv;
	}

	@RequestMapping(value = "/add/address", method = RequestMethod.GET)
	public ModelAndView goAddAddress(@RequestParam(name = "address", required = true) String addressType) {
		ModelAndView mv = new ModelAndView("home");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = accountDAO.get(auth.getName());

		Address newAddress = new Address();
		mv.addObject("title", "Address Management");
		mv.addObject("userClickedAddAddress", true);
		mv.addObject("address", newAddress);
		mv.addObject("addressType", addressType);
		return mv;
	}

	@RequestMapping(value = "/add/address", method = RequestMethod.POST)
	public String addAddress(@Valid @ModelAttribute("address") Address address, BindingResult results) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = accountDAO.get(auth.getName());
		if (!results.hasErrors()) {
			address.setAccountId(currentUser.getId());
			addressDAO.addAddress(address);
			if (address.isBilling()) {
				return "redirect:/profile/show?success=billing_address";
			} else {
				return "redirect:/profile/show?success=shipping_address";
			}
		} else {
			return "redirect:/profile/show";
		}
	}

	@RequestMapping(value = "/update/address", method = RequestMethod.POST)
	public String updateAddress(@Valid @ModelAttribute("address") Address address, BindingResult results) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = accountDAO.get(auth.getName());

		if (!results.hasErrors()) {

			addressDAO.updateAddress(address);

			return "redirect:/profile/show?success=updating_billing_address";

		} else {
			return "redirect:/profile/show?error=updating_billing_address";
		}
	}

	@RequestMapping(value = "/update/address", method = RequestMethod.GET)
	public ModelAndView goUpdateAddress() {
		ModelAndView mv = new ModelAndView("home");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = accountDAO.get(auth.getName());

		Address currentAddress = addressDAO.getBillingAddress(currentUser.getId());
		if (currentAddress != null) {
			mv.addObject("title", "Address Management");
			mv.addObject("userClickedUpdateAddress", true);
			mv.addObject("address", currentAddress);
			return mv;
		} else {
			Address newAddress = new Address();
			mv.addObject("title", "Address Management");
			mv.addObject("userClickedAddAddress", true);
			mv.addObject("address", newAddress);
			return mv;
		}
	}

	@RequestMapping(value = "/addPhoto", method = RequestMethod.POST, headers = "content-type=multipart/*")
	public String addPhoto(@RequestParam("file") MultipartFile file) {

		AccountModel model = (AccountModel) session.getAttribute("accountModel");

		if (file != null && !file.getOriginalFilename().equals("")) {
			FileUploadUtility.UploadFile(file, "Account_" + model.getId());
			return "redirect:/profile/show?success=photo_upload";
		} else {
			return "redirect:/profile/show?error=photo_upload";
		}

	}

	/*
	 * * @ModelAttribute("billingAddress") public Address getBillingAddress() {
	 * 
	 * }
	 */

}
