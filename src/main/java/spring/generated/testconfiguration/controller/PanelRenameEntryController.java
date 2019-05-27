package spring.generated.testconfiguration.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import spring.generated.testconfiguration.form.PanelRenameEntryForm;
import spring.mine.common.controller.BaseController;
import spring.service.localization.LocalizationService;
import spring.service.panel.PanelService;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.localization.valueholder.Localization;
import us.mn.state.health.lims.panel.valueholder.Panel;

@Controller
public class PanelRenameEntryController extends BaseController {
	
	@Autowired
	PanelService panelService;
	@Autowired
	LocalizationService localizationService;
	

	@RequestMapping(value = "/PanelRenameEntry", method = RequestMethod.GET)
	public ModelAndView showPanelRenameEntry(HttpServletRequest request) {
		PanelRenameEntryForm form = new PanelRenameEntryForm();
		form.setPanelList(DisplayListService.getInstance().getList(DisplayListService.ListType.PANELS));

		return findForward(FWD_SUCCESS, form);
	}

	@Override
	protected String findLocalForward(String forward) {
		if (FWD_SUCCESS.equals(forward)) {
			return "panelRenameDefinition";
		} else if (FWD_SUCCESS_INSERT.equals(forward)) {
			return "redirect:/PanelRenameEntry.do";
		} else if (FWD_FAIL_INSERT.equals(forward)) {
			return "panelRenameDefinition";
		} else {
			return "PageNotFound";
		}
	}

	@RequestMapping(value = "/PanelRenameEntry", method = RequestMethod.POST)
	public ModelAndView updatePanelRenameEntry(HttpServletRequest request,
			@ModelAttribute("form") @Valid PanelRenameEntryForm form, BindingResult result) {
		if (result.hasErrors()) {
			saveErrors(result);
			form.setPanelList(DisplayListService.getInstance().getList(DisplayListService.ListType.PANELS));
			return findForward(FWD_FAIL_INSERT, form);
		}

		String panelId = form.getPanelId();
		String nameEnglish = form.getNameEnglish();
		String nameFrench = form.getNameFrench();
		String userId = getSysUserId(request);

		updatePanelNames(panelId, nameEnglish, nameFrench, userId);

		return findForward(FWD_SUCCESS_INSERT, form);
	}

	private void updatePanelNames(String panelId, String nameEnglish, String nameFrench, String userId) {
//		PanelDAO panelDAO = new PanelDAOImpl();
		Panel panel = panelService.getPanelById(panelId);

		if (panel != null) {

			Localization name = panel.getLocalization();
			name.setEnglish(nameEnglish.trim());
			name.setFrench(nameFrench.trim());
			name.setSysUserId(userId);

//			Transaction tx = HibernateUtil.getSession().beginTransaction();

			try {
//				new LocalizationDAOImpl().updateData(name);
				localizationService.update(name);
//				tx.commit();
			} catch (HibernateException e) {
//				tx.rollback();
				e.printStackTrace();
			} 
//			finally {
//				HibernateUtil.closeSession();
//			}

		}

		// Refresh panel names
		DisplayListService.getInstance().getFreshList(DisplayListService.ListType.PANELS);
	}

	@Override
	protected String getPageTitleKey() {
		return null;
	}

	@Override
	protected String getPageSubtitleKey() {
		return null;
	}
}
