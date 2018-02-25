package nb.controller;

import nb.domain.*;
import nb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@SessionAttributes({"userId"})
public class DirectController {

    @Autowired
    private CreditService creditService;
    @Autowired
    private LotService lotService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private BidService bidService;
    @Autowired
    private AssetService assetService;

    private boolean isAuth(HttpSession session) {
        Locale.setDefault(Locale.ENGLISH);
        return session.getAttribute("userId") != null;
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    private String main(HttpSession session) {
        Locale.setDefault(Locale.ENGLISH);
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            return "Menu";
        }
    }

    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.HEAD})
    private String index(HttpSession session) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            return "Menu";
        }
    }

    @RequestMapping(value = "/reports", method = {RequestMethod.GET, RequestMethod.HEAD})
    private String reports(HttpSession session) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            return "Reports";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private @ResponseBody
    String logCheck(@RequestParam("login") String login,
                    @RequestParam("password") String password,
                    Model uID) {
        if (userService.isExist(login, password)) {
            uID.addAttribute("userId", login);
            return "1";
        } else {
            return "0";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    private String logOut( HttpSession session) {
        session.removeAttribute("userId");
            return "LogIN";
    }

    @RequestMapping(value = "/lotMenu/{saleStatus}", method = RequestMethod.GET)
    private String lotMenu(HttpSession session, Model model, @PathVariable String saleStatus) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {

            switch (saleStatus) {
                case "notSolded":
                    model.addAttribute("lotList", lotService.getNotSoldedLots());

                    break;
                case "solded":
                    model.addAttribute("lotList", lotService.getSoldedLots());
                    break;
                default:
                    model.addAttribute("lotList", lotService.getLots());
                    break;
            }
            model.addAttribute(saleStatus);
            return "LotMenu";
        }
    }

    @RequestMapping(value = "/exMenu", method = RequestMethod.GET)
    private String exMenu(HttpSession session, Model model) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            model.addAttribute("exchangesList", exchangeService.getAllExchanges());
            return "ExMenu";
        }
    }

    @RequestMapping(value = "/bidMenu", method = RequestMethod.GET)
    private String bidMenu(HttpSession session, Model model) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            model.addAttribute("bidList", bidService.getAllBids());
            model.addAttribute("exchangeList", exchangeService.getAllExchanges());
           /* model.addAttribute("bidStatusList", bidStatusList);*/
            return "BidMenu";
        }
    }

    /*@RequestMapping(value = "/assets", method = RequestMethod.GET)
    private String assets(HttpSession session, Model model) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            int portionNum;
            try {
                String p = (String) session.getAttribute("assetPortionNum");
                portionNum = Integer.parseInt(p);
            } catch (Exception e) {
                portionNum = 0;
            }
            model.addAttribute("assetPortion", portionNum + 1);
            model.addAttribute("assetList", assetService.getAssetsByPortion(portionNum));
            model.addAttribute("fondDecisionsList", StaticStatus.fondDecisionsList);
            model.addAttribute("allBidDates", assetService.getAllBidDates());
            model.addAttribute("bidResultList", StaticStatus.bidResultList);
            model.addAttribute("workStages", StaticStatus.statusList);
            model.addAttribute("exchangeList", assetService.getExchanges());
            model.addAttribute("decisionNumbers", assetService.getDecisionNumbers());
            model.addAttribute("allLotId", lotService.getLotsId());
            model.addAttribute("allExchangeList", exchangeService.getAllExchanges());
            return "Assets";
        }
    }*/

    @RequestMapping(value = "/credits", method = RequestMethod.GET)
    private String credits(HttpSession session, Model model) {
        if (!isAuth(session)) {
            return "LogIN";
        } else
            model.addAttribute("totalCountOfCredits", creditService.getTotalCountOfCredits());

        return "Credits";
    }

    @RequestMapping(value = "/lotCreator", method = RequestMethod.GET)
    private String singleFormLot(HttpSession session, Model m) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            List<Asset> assetList = new ArrayList<>();
            m.addAttribute("assetList", assetList);
            m.addAttribute("lotType", 1);
            return "LotCreator";
        }
    }

    @RequestMapping(value = "/lotCreator1", method = RequestMethod.GET)
    private String singleFormLot1(HttpSession session, Model m) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            List<Asset> assetList = new ArrayList<>();
            String[] idMass = (String[]) session.getAttribute("assetsListToLot");
            for (String id : idMass) {
                assetList.add(assetService.getAsset(Long.parseLong(id)));
            }
            m.addAttribute("assetList", assetList);
            m.addAttribute("lotType", 1);
            return "LotCreator";
        }
    }

    @RequestMapping(value = "/lotCreditsCreator", method = RequestMethod.GET)
    private String lotCreditsCreator(HttpSession session, Model m) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            List<Credit> creditList = new ArrayList<>();
            m.addAttribute("creditList", creditList);
            m.addAttribute("lotType", 0);
            //return "LotCreditsCreator";
            return "LotCreator";
        }
    }

    @RequestMapping(value = "/lotCreditsCreator1", method = RequestMethod.GET)
    private String lotCreditsCreator1(HttpSession session, Model m) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            List<Credit> creditList = new ArrayList<>();
            String[] idMass = (String[]) session.getAttribute("creditsListToLot");
            System.out.println("idMass "+ Arrays.toString(idMass));
            for (String id : idMass) {
                Credit cr = (Credit)creditService.getCreditsByIdBars(Long.parseLong(id)).get(0);
                /*if(cr.getLot()==null&&cr.getFondDecisionDate()!=null)*/
                creditList.add(cr);
            }

            m.addAttribute("creditList", creditList);
            m.addAttribute("lotType", 0);
            //return "LotCreditsCreator";
            return "LotCreator";
        }
    }

   /* @RequestMapping(value = "/lotRedactor", method = RequestMethod.GET)
    private String LotRedactor(HttpSession session, Model model) {
        String lotId = (String) session.getAttribute("lotRid");
        String userName = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));

        model.addAttribute("bidStatusList", StaticStatus.bidStatusList);
        model.addAttribute("statusList", StaticStatus.statusList);
        model.addAttribute("lott", lot);
        model.addAttribute("user", userName);
        model.addAttribute("bidResultList", StaticStatus.bidResultList);
        model.addAttribute("allBidsList", bidService.getAllBids());
        model.addAttribute("fondDecisionsList", StaticStatus.fondDecisionsList);
        model.addAttribute("allExchangeList", exchangeService.getAllExchanges());
        List<Long> bidIdList = lotService.getBidsIdByLot(Long.parseLong(lotId));

        Set<Bid> historyBids = new TreeSet<>();

        for (long id : bidIdList) {
            Bid bid = bidService.getBid(id);

            if (lot.getBid() == null || lot.getBid().getId() != id) {
                try{
                historyBids.add(bid);
                }
                catch (NullPointerException e){

                }
            }
        }
        model.addAttribute("bidsHistoryList", historyBids);
        return "LotRedaction";

    }*/

    @RequestMapping(value = "/exLots", method = RequestMethod.GET)
    private String exRedactor(HttpSession session, Model model) {
        String exId = (String) session.getAttribute("exRid");
        Exchange exchange = exchangeService.getExchange(Long.parseLong(exId));
        List<Bid> bidList = bidService.getBidsByExchange(exchange);

        List<Lot> lotList = new ArrayList<>();
        for (Bid bid : bidList) {
            lotList.addAll(lotService.getLotsByBid(bid));
        }
        model.addAttribute("exchange", exchange);
        model.addAttribute("lotList", lotList);
        return "ExLots";
    }

    @RequestMapping(value = "/assetsSearch", method = RequestMethod.GET)
    private String assetsSearch(HttpSession session) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            return "AssetsSearch";
        }
    }
    @RequestMapping(value = "/creditsSearch", method = RequestMethod.GET)
    private String creditsSearch(HttpSession session) {
        if (!isAuth(session)) {
            return "LogIN";
        } else {
            return "CreditsSearch";
        }
    }
}
