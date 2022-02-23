package com.bloobirds.dashboards.ui;

import com.bloobirds.dashboards.User;
import com.bloobirds.dashboards.datamodel.SalesUser;
import com.bloobirds.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.dashboards.datamodel.service.SalesUserService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.badge.Badge;

import java.util.Random;

@Route(value = "/salesUser", layout = MainView.class)
@PageTitle("Users")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
public class SalesUserView extends Div {

    private static final SerializableBiConsumer<Badge, SalesUser> statusComponentUpdater = (badge, user) -> {
        Integer status = user.getStatus();

        switch (status) {
            case SalesUser.STATUS_ACTIVE -> {
                badge.setText(User.getMessage("salesuser.status.active"));
                badge.setVariant(Badge.BadgeVariant.SUCCESS);
            }
            case SalesUser.STATUS_INACTIVE -> {
                badge.setText(User.getMessage("salesuser.status.inactive"));
                badge.setVariant(Badge.BadgeVariant.ERROR);
            }
            default -> {
                String label = User.getMessage("salesuser.status." + status);
                if (label == null) label = User.getMessage("salesuser.status.nostatus");
                badge.setText(label);
                badge.setVariant(Badge.BadgeVariant.CONTRAST);
            }
        }
    };

    public SalesUserView(@Autowired SalesUserService service) {
        var grid = new EnhancedGrid<SalesUser>();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(salesUser -> salesUser.getObjectID().getTenantID()).setHeader(User.getMessage("tenant")).setAutoWidth(true).setResizable(true);
        grid.addColumn(contact -> contact.getObjectID().getBBobjectID()).setHeader(User.getMessage("id")).setAutoWidth(true).setResizable(true);
        grid.addColumn(SalesUser::getName).setHeader(User.getMessage("salesuser.name")).setAutoWidth(true);
        grid.addColumn(SalesUser::getSurname).setHeader(User.getMessage("salesuser.surname")).setAutoWidth(true);
        grid.addColumn(SalesUser::getPhoneNumber).setHeader(User.getMessage("salesuser.phone")).setAutoWidth(true);
        grid.addColumn(SalesUser::getEmail).setHeader(User.getMessage("salesuser.email")).setAutoWidth(true);
        grid.addColumn(createStatusComponentRenderer()).setHeader(User.getMessage("salesuser.status")).setAutoWidth(true);

        int fields= Integer.parseInt(User.getMessage("salesuser.datamodel.fields"));
        if(fields>0) {
            grid.addComponentColumn(contact -> {
                Button details = new Button(User.getMessage("application.more"));

                details.addClickListener(e -> {
                    Dialog dialog = new Dialog(); //@todo not nice
                    dialog.getElement().setAttribute("aria-label", User.getMessage("salesuser.details"));
                    VerticalLayout dialogLayout = createDialogLayout(contact, dialog);
                    dialog.add(dialogLayout);
                    dialog.setModal(false);
                    dialog.setDraggable(true);
                    dialog.open();
                });
                details.setEnabled(true);
                return details;
            });
        }

        grid.setItems(query -> service.findAll(query.getPage(), query.getPageSize()));

        Div messageDiv = new Div();
        grid.asSingleSelect().addValueChangeListener(event -> {
            SalesUser current = event.getValue();
// @TODO wrong!!
            if (current == null) current = new SalesUser();

            SalesUser old = event.getOldValue();
            if (old == null) old = new SalesUser();
            String message = String.format("Selection changed from [%s|%s] to [%s|%s]",
                    old.getObjectID().getTenantID(), old.getObjectID().getBBobjectID(),
                    current.getObjectID().getTenantID(), current.getObjectID().getBBobjectID());
            messageDiv.setText(message);
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        // add button to generate test data
        Button testDataButton = new Button(User.getMessage("application.generatedata"), e -> generateTestData(service));
        horizontalLayout.add(testDataButton);
        add(horizontalLayout, grid, messageDiv);

        setSizeFull();
    }

    private static ComponentRenderer<Badge, SalesUser> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Badge::new, statusComponentUpdater);
    }

    private VerticalLayout createDialogLayout(SalesUser person, Dialog dialog) {
        H2 headline = new H2(User.getMessage("salesuser.details"));
        headline.getStyle().set("margin", "0").set("font-size", "1.5em")
                .set("font-weight", "bold");
        HorizontalLayout header = new HorizontalLayout(headline);
        header.getElement().getClassList().add("draggable");
        header.setSpacing(false);
        header.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("cursor", "move");
        header.getStyle()
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("margin",
                        "calc(var(--lumo-space-s) * -1) calc(var(--lumo-space-l) * -1) 0");

        int fields= Integer.parseInt(User.getMessage("salesuser.datamodel.fields"));

        VerticalLayout fieldLayout = new VerticalLayout();
        for (int i=0;i<fields;i++){
            String field= User.getMessage("salesuser.field."+i);
            TextField textField=new TextField(field);
            textField.setValue(person.getAttribute(field));
            textField.setEnabled(false);
            fieldLayout.add(textField);
        }
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        Button saveButton = new Button(User.getMessage("application.close"), e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(header, fieldLayout,
                buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }

    private void generateTestData(SalesUserService service) {
        BBObjectID BBid= new BBObjectID();
        Random rand= new Random();
        BBid.setTenantID("bloobirds");
        SalesUser c= new SalesUser();
        c.setPhoneNumber("+34555555555");


        BBid.setBBobjectID(rand.nextLong(10000));
        c.setObjectID(BBid);
        c.setName(names[rand.nextInt(2000)].toLowerCase());
        c.setSurname(names[rand.nextInt(2000)].toLowerCase());
        c.setEmail(c.getName().substring(0)+c.getSurname()+"@bloobirds.info");
        c.setStatus(rand.nextInt(4));
        c.addAttribute("Role",roles[rand.nextInt(10)]);
        c.addAttribute("Function",functions[rand.nextInt(6)]);
        c=service.save(c);

        BBid.setBBobjectID(rand.nextLong(10000));
        c.setObjectID(BBid);
        c.setName(names[rand.nextInt(2000)].toLowerCase());
        c.setSurname(names[rand.nextInt(2000)].toLowerCase());
        c.setEmail(c.getName().substring(0)+c.getSurname()+"@bloobirds.info");
        c.setStatus(rand.nextInt(3));
        c.addAttribute("Role",roles[rand.nextInt(10)]);
        c.addAttribute("Function",functions[rand.nextInt(6)]);
        c=service.save(c);

        BBid.setBBobjectID(rand.nextLong(10000));
        c.setObjectID(BBid);
        c.setName(names[rand.nextInt(2000)].toLowerCase());
        c.setSurname(names[rand.nextInt(2000)].toLowerCase());
        c.setEmail(c.getName().substring(0)+c.getSurname()+"@bloobirds.info");
        c.setStatus(rand.nextInt(4));
        c.addAttribute("Role",roles[rand.nextInt(10)]);
        c.addAttribute("Function",functions[rand.nextInt(6)]);
        c=service.save(c);

        BBid.setBBobjectID(rand.nextLong(10000));
        c.setObjectID(BBid);
        c.setName(names[rand.nextInt(2000)].toLowerCase());
        c.setSurname(names[rand.nextInt(2000)].toLowerCase());
        c.setEmail(c.getName().substring(0)+c.getSurname()+"@bloobirds.info");
        c.setStatus(rand.nextInt(4));
        c.addAttribute("Role",roles[rand.nextInt(10)]);
        c.addAttribute("Function",functions[rand.nextInt(6)]);
        c=service.save(c);

        BBid.setBBobjectID(rand.nextLong(10000));
        c.setObjectID(BBid);
        c.setName(names[rand.nextInt(2000)].toLowerCase());
        c.setSurname(names[rand.nextInt(2000)].toLowerCase());
        c.setEmail(c.getName().substring(0)+c.getSurname()+"@bloobirds.info");
        c.setStatus(rand.nextInt(4));
        c.addAttribute("Role",roles[rand.nextInt(10)]);
        c.addAttribute("Function",functions[rand.nextInt(6)]);
        service.save(c);
    }

    String[] names = {"SMITH","JOHNSON","WILLIAMS","JONES","BROWN","DAVIS","MILLER","WILSON","MOORE","TAYLOR","ANDERSON","THOMAS","JACKSON","WHITE","HARRIS","MARTIN","THOMPSON","GARCIA","MARTINEZ","ROBINSON","CLARK","RODRIGUEZ","LEWIS","LEE","WALKER","HALL","ALLEN","YOUNG","HERNANDEZ","KING","WRIGHT","LOPEZ","HILL","SCOTT","GREEN","ADAMS","BAKER","GONZALEZ","NELSON","CARTER","MITCHELL","PEREZ","ROBERTS","TURNER","PHILLIPS","CAMPBELL","PARKER","EVANS","EDWARDS","COLLINS","STEWART","SANCHEZ","MORRIS","ROGERS","REED","COOK","MORGAN","BELL","MURPHY","BAILEY","RIVERA","COOPER","RICHARDSON","COX","HOWARD","WARD","TORRES","PETERSON","GRAY","RAMIREZ","JAMES","WATSON","BROOKS","KELLY","SANDERS","PRICE","BENNETT","WOOD","BARNES","ROSS","HENDERSON","COLEMAN","JENKINS","PERRY","POWELL","LONG","PATTERSON","HUGHES","FLORES","WASHINGTON","BUTLER","SIMMONS","FOSTER","GONZALES","BRYANT","ALEXANDER","RUSSELL","GRIFFIN","DIAZ","HAYES","MYERS","FORD","HAMILTON","GRAHAM","SULLIVAN","WALLACE","WOODS","COLE","WEST","JORDAN","OWENS","REYNOLDS","FISHER","ELLIS","HARRISON","GIBSON","MCDONALD","CRUZ","MARSHALL","ORTIZ","GOMEZ","MURRAY","FREEMAN","WELLS","WEBB","SIMPSON","STEVENS","TUCKER","PORTER","HUNTER","HICKS","CRAWFORD","HENRY","BOYD","MASON","MORALES","KENNEDY","WARREN","DIXON","RAMOS","REYES","BURNS","GORDON","SHAW","HOLMES","RICE","ROBERTSON","HUNT","BLACK","DANIELS","PALMER","MILLS","NICHOLS","GRANT","KNIGHT","FERGUSON","ROSE","STONE","HAWKINS","DUNN","PERKINS","HUDSON","SPENCER","GARDNER","STEPHENS","PAYNE","PIERCE","BERRY","MATTHEWS","ARNOLD","WAGNER","WILLIS","RAY","WATKINS","OLSON","CARROLL","DUNCAN","SNYDER","HART","CUNNINGHAM","BRADLEY","LANE","ANDREWS","RUIZ","HARPER","FOX","RILEY","ARMSTRONG","CARPENTER","WEAVER","GREENE","LAWRENCE","ELLIOTT","CHAVEZ","SIMS","AUSTIN","PETERS","KELLEY","FRANKLIN","LAWSON","FIELDS","GUTIERREZ","RYAN","SCHMIDT","CARR","VASQUEZ","CASTILLO","WHEELER","CHAPMAN","OLIVER","MONTGOMERY","RICHARDS","WILLIAMSON","JOHNSTON","BANKS","MEYER","BISHOP","MCCOY","HOWELL","ALVAREZ","MORRISON","HANSEN","FERNANDEZ","GARZA","HARVEY","LITTLE","BURTON","STANLEY","NGUYEN","GEORGE","JACOBS","REID","KIM","FULLER","LYNCH","DEAN","GILBERT","GARRETT","ROMERO","WELCH","LARSON","FRAZIER","BURKE","HANSON","DAY","MENDOZA","MORENO","BOWMAN","MEDINA","FOWLER","BREWER","HOFFMAN","CARLSON","SILVA","PEARSON","HOLLAND","DOUGLAS","FLEMING","JENSEN","VARGAS","BYRD","DAVIDSON","HOPKINS","MAY","TERRY","HERRERA","WADE","SOTO","WALTERS","CURTIS","NEAL","CALDWELL","LOWE","JENNINGS","BARNETT","GRAVES","JIMENEZ","HORTON","SHELTON","BARRETT","OBRIEN","CASTRO","SUTTON","GREGORY","MCKINNEY","LUCAS","MILES","CRAIG","RODRIQUEZ","CHAMBERS","HOLT","LAMBERT","FLETCHER","WATTS","BATES","HALE","RHODES","PENA","BECK","NEWMAN","HAYNES","MCDANIEL","MENDEZ","BUSH","VAUGHN","PARKS","DAWSON","SANTIAGO","NORRIS","HARDY","LOVE","STEELE","CURRY","POWERS","SCHULTZ","BARKER","GUZMAN","PAGE","MUNOZ","BALL","KELLER","CHANDLER","WEBER","LEONARD","WALSH","LYONS","RAMSEY","WOLFE","SCHNEIDER","MULLINS","BENSON","SHARP","BOWEN","DANIEL","BARBER","CUMMINGS","HINES","BALDWIN","GRIFFITH","VALDEZ","HUBBARD","SALAZAR","REEVES","WARNER","STEVENSON","BURGESS","SANTOS","TATE","CROSS","GARNER","MANN","MACK","MOSS","THORNTON","DENNIS","MCGEE","FARMER","DELGADO","AGUILAR","VEGA","GLOVER","MANNING","COHEN","HARMON","RODGERS","ROBBINS","NEWTON","TODD","BLAIR","HIGGINS","INGRAM","REESE","CANNON","STRICKLAND","TOWNSEND","POTTER","GOODWIN","WALTON","ROWE","HAMPTON","ORTEGA","PATTON","SWANSON","JOSEPH","FRANCIS","GOODMAN","MALDONADO","YATES","BECKER","ERICKSON","HODGES","RIOS","CONNER","ADKINS","WEBSTER","NORMAN","MALONE","HAMMOND","FLOWERS","COBB","MOODY","QUINN","BLAKE","MAXWELL","POPE","FLOYD","OSBORNE","PAUL","MCCARTHY","GUERRERO","LINDSEY","ESTRADA","SANDOVAL","GIBBS","TYLER","GROSS","FITZGERALD","STOKES","DOYLE","SHERMAN","SAUNDERS","WISE","COLON","GILL","ALVARADO","GREER","PADILLA","SIMON","WATERS","NUNEZ","BALLARD","SCHWARTZ","MCBRIDE","HOUSTON","CHRISTENSEN","KLEIN","PRATT","BRIGGS","PARSONS","MCLAUGHLIN","ZIMMERMAN","FRENCH","BUCHANAN","MORAN","COPELAND","ROY","PITTMAN","BRADY","MCCORMICK","HOLLOWAY","BROCK","POOLE","FRANK","LOGAN","OWEN","BASS","MARSH","DRAKE","WONG","JEFFERSON","PARK","MORTON","ABBOTT","SPARKS","PATRICK","NORTON","HUFF","CLAYTON","MASSEY","LLOYD","FIGUEROA","CARSON","BOWERS","ROBERSON","BARTON","TRAN","LAMB","HARRINGTON","CASEY","BOONE","CORTEZ","CLARKE","MATHIS","SINGLETON","WILKINS","CAIN","BRYAN","UNDERWOOD","HOGAN","MCKENZIE","COLLIER","LUNA","PHELPS","MCGUIRE","ALLISON","BRIDGES","WILKERSON","NASH","SUMMERS","ATKINS","WILCOX","PITTS","CONLEY","MARQUEZ","BURNETT","RICHARD","COCHRAN","CHASE","DAVENPORT","HOOD","GATES","CLAY","AYALA","SAWYER","ROMAN","VAZQUEZ","DICKERSON","HODGE","ACOSTA","FLYNN","ESPINOZA","NICHOLSON","MONROE","WOLF","MORROW","KIRK","RANDALL","ANTHONY","WHITAKER","OCONNOR","SKINNER","WARE","MOLINA","KIRBY","HUFFMAN","BRADFORD","CHARLES","GILMORE","DOMINGUEZ","ONEAL","BRUCE","LANG","COMBS","KRAMER","HEATH","HANCOCK","GALLAGHER","GAINES","SHAFFER","SHORT","WIGGINS","MATHEWS","MCCLAIN","FISCHER","WALL","SMALL","MELTON","HENSLEY","BOND","DYER","CAMERON","GRIMES","CONTRERAS","CHRISTIAN","WYATT","BAXTER","SNOW","MOSLEY","SHEPHERD","LARSEN","HOOVER","BEASLEY","GLENN","PETERSEN","WHITEHEAD","MEYERS","KEITH","GARRISON","VINCENT","SHIELDS","HORN","SAVAGE","OLSEN","SCHROEDER","HARTMAN","WOODARD","MUELLER","KEMP","DELEON","BOOTH","PATEL","CALHOUN","WILEY","EATON","CLINE","NAVARRO","HARRELL","LESTER","HUMPHREY","PARRISH","DURAN","HUTCHINSON","HESS","DORSEY","BULLOCK","ROBLES","BEARD","DALTON","AVILA","VANCE","RICH","BLACKWELL","YORK","JOHNS","BLANKENSHIP","TREVINO","SALINAS","CAMPOS","PRUITT","MOSES","CALLAHAN","GOLDEN","MONTOYA","HARDIN","GUERRA","MCDOWELL","CAREY","STAFFORD","GALLEGOS","HENSON","WILKINSON","BOOKER","MERRITT","MIRANDA","ATKINSON","ORR","DECKER","HOBBS","PRESTON","TANNER","KNOX","PACHECO","STEPHENSON","GLASS","ROJAS","SERRANO","MARKS","HICKMAN","ENGLISH","SWEENEY","STRONG","PRINCE","MCCLURE","CONWAY","WALTER","ROTH","MAYNARD","FARRELL","LOWERY","HURST","NIXON","WEISS","TRUJILLO","ELLISON","SLOAN","JUAREZ","WINTERS","MCLEAN","RANDOLPH","LEON","BOYER","VILLARREAL","MCCALL","GENTRY","CARRILLO","KENT","AYERS","LARA","SHANNON","SEXTON","PACE","HULL","LEBLANC","BROWNING","VELASQUEZ","LEACH","CHANG","HOUSE","SELLERS","HERRING","NOBLE","FOLEY","BARTLETT","MERCADO","LANDRY","DURHAM","WALLS","BARR","MCKEE","BAUER","RIVERS","EVERETT","BRADSHAW","PUGH","VELEZ","RUSH","ESTES","DODSON","MORSE","SHEPPARD","WEEKS","CAMACHO","BEAN","BARRON","LIVINGSTON","MIDDLETON","SPEARS","BRANCH","BLEVINS","CHEN","KERR","MCCONNELL","HATFIELD","HARDING","ASHLEY","SOLIS","HERMAN","FROST","GILES","BLACKBURN","WILLIAM","PENNINGTON","WOODWARD","FINLEY","MCINTOSH","KOCH","BEST","SOLOMON","MCCULLOUGH","DUDLEY","NOLAN","BLANCHARD","RIVAS","BRENNAN","MEJIA","KANE","BENTON","JOYCE","BUCKLEY","HALEY","VALENTINE","MADDOX","RUSSO","MCKNIGHT","BUCK","MOON","MCMILLAN","CROSBY","BERG","DOTSON","MAYS","ROACH","CHURCH","CHAN","RICHMOND","MEADOWS","FAULKNER","ONEILL","KNAPP","KLINE","BARRY","OCHOA","JACOBSON","GAY","AVERY","HENDRICKS","HORNE","SHEPARD","HEBERT","CHERRY","CARDENAS","MCINTYRE","WHITNEY","WALLER","HOLMAN","DONALDSON","CANTU","TERRELL","MORIN","GILLESPIE","FUENTES","TILLMAN","SANFORD","BENTLEY","PECK","KEY","SALAS","ROLLINS","GAMBLE","DICKSON","BATTLE","SANTANA","CABRERA","CERVANTES","HOWE","HINTON","HURLEY","SPENCE","ZAMORA","YANG","MCNEIL","SUAREZ","CASE","PETTY","GOULD","MCFARLAND","SAMPSON","CARVER","BRAY","ROSARIO","MACDONALD","STOUT","HESTER","MELENDEZ","DILLON","FARLEY","HOPPER","GALLOWAY","POTTS","BERNARD","JOYNER","STEIN","AGUIRRE","OSBORN","MERCER","BENDER","FRANCO","ROWLAND","SYKES","BENJAMIN","TRAVIS","PICKETT","CRANE","SEARS","MAYO","DUNLAP","HAYDEN","WILDER","MCKAY","COFFEY","MCCARTY","EWING","COOLEY","VAUGHAN","BONNER","COTTON","HOLDER","STARK","FERRELL","CANTRELL","FULTON","LYNN","LOTT","CALDERON","ROSA","POLLARD","HOOPER","BURCH","MULLEN","FRY","RIDDLE","LEVY","DAVID","DUKE","ODONNELL","GUY","MICHAEL","BRITT","FREDERICK","DAUGHERTY","BERGER","DILLARD","ALSTON","JARVIS","FRYE","RIGGS","CHANEY","ODOM","DUFFY","FITZPATRICK","VALENZUELA","MERRILL","MAYER","ALFORD","MCPHERSON","ACEVEDO","DONOVAN","BARRERA","ALBERT","COTE","REILLY","COMPTON","RAYMOND","MOONEY","MCGOWAN","CRAFT","CLEVELAND","CLEMONS","WYNN","NIELSEN","BAIRD","STANTON","SNIDER","ROSALES","BRIGHT","WITT","STUART","HAYS","HOLDEN","RUTLEDGE","KINNEY","CLEMENTS","CASTANEDA","SLATER","HAHN","EMERSON","CONRAD","BURKS","DELANEY","PATE","LANCASTER","SWEET","JUSTICE","TYSON","SHARPE","WHITFIELD","TALLEY","MACIAS","IRWIN","BURRIS","RATLIFF","MCCRAY","MADDEN","KAUFMAN","BEACH","GOFF","CASH","BOLTON","MCFADDEN","LEVINE","GOOD","BYERS","KIRKLAND","KIDD","WORKMAN","CARNEY","DALE","MCLEOD","HOLCOMB","ENGLAND","FINCH","HEAD","BURT","HENDRIX","SOSA","HANEY","FRANKS","SARGENT","NIEVES","DOWNS","RASMUSSEN","BIRD","HEWITT","LINDSAY","LE","FOREMAN","VALENCIA","ONEIL","DELACRUZ","VINSON","DEJESUS","HYDE","FORBES","GILLIAM","GUTHRIE","WOOTEN","HUBER","BARLOW","BOYLE","MCMAHON","BUCKNER","ROCHA","PUCKETT","LANGLEY","KNOWLES","COOKE","VELAZQUEZ","WHITLEY","NOEL","VANG","SHEA","ROUSE","HARTLEY","MAYFIELD","ELDER","RANKIN","HANNA","COWAN","LUCERO","ARROYO","SLAUGHTER","HAAS","OCONNELL","MINOR","KENDRICK","SHIRLEY","KENDALL","BOUCHER","ARCHER","BOGGS","ODELL","DOUGHERTY","ANDERSEN","NEWELL","CROWE","WANG","FRIEDMAN","BLAND","SWAIN","HOLLEY","FELIX","PEARCE","CHILDS","YARBROUGH","GALVAN","PROCTOR","MEEKS","LOZANO","MORA","RANGEL","BACON","VILLANUEVA","SCHAEFER","ROSADO","HELMS","BOYCE","GOSS","STINSON","SMART","LAKE","IBARRA","HUTCHINS","COVINGTON","REYNA","GREGG","WERNER","CROWLEY","HATCHER","MACKEY","BUNCH","WOMACK","POLK","JAMISON","DODD","CHILDRESS","CHILDERS","CAMP","VILLA","DYE","SPRINGER","MAHONEY","DAILEY","BELCHER","LOCKHART","GRIGGS","COSTA","CONNOR","BRANDT","WINTER","WALDEN","MOSER","TRACY","TATUM","MCCANN","AKERS","LUTZ","PRYOR","LAW","OROZCO","MCALLISTER","LUGO","DAVIES","SHOEMAKER","MADISON","RUTHERFORD","NEWSOME","MAGEE","CHAMBERLAIN","BLANTON","SIMMS","GODFREY","FLANAGAN","CRUM","CORDOVA","ESCOBAR","DOWNING","SINCLAIR","DONAHUE","KRUEGER","MCGINNIS","GORE","FARRIS","WEBBER","CORBETT","ANDRADE","STARR","LYON","YODER","HASTINGS","MCGRATH","SPIVEY","KRAUSE","HARDEN","CRABTREE","KIRKPATRICK","HOLLIS","BRANDON","ARRINGTON","ERVIN","CLIFTON","RITTER","MCGHEE","BOLDEN","MALONEY","GAGNON","DUNBAR","PONCE","PIKE","MAYES","HEARD","BEATTY","MOBLEY","KIMBALL","BUTTS","MONTES","HERBERT","GRADY","ELDRIDGE","BRAUN","HAMM","GIBBONS","SEYMOUR","MOYER","MANLEY","HERRON","PLUMMER","ELMORE","CRAMER","GARY","RUCKER","HILTON","BLUE","PIERSON","FONTENOT","FIELD","RUBIO","GRACE","GOLDSTEIN","ELKINS","WILLS","NOVAK","JOHN","HICKEY","WORLEY","GORMAN","KATZ","DICKINSON","BROUSSARD","FRITZ","WOODRUFF","CROW","CHRISTOPHER","BRITTON","FORREST","NANCE","LEHMAN","BINGHAM","ZUNIGA","WHALEY","SHAFER","COFFMAN","STEWARD","DELAROSA","NIX","NEELY","NUMBERS","MATA","MANUEL","DAVILA","MCCABE","KESSLER","EMERY","BOWLING","HINKLE","WELSH","PAGAN","GOLDBERG","GOINS","CROUCH","CUEVAS","QUINONES","MCDERMOTT","HENDRICKSON","SAMUELS","DENTON","BERGERON","LAM","IVEY","LOCKE","HAINES","THURMAN","SNELL","HOSKINS","BYRNE","MILTON","WINSTON","ARTHUR","ARIAS","STANFORD","ROE","CORBIN","BELTRAN","CHAPPELL","HURT","DOWNEY","DOOLEY","TUTTLE","COUCH","PAYTON","MCELROY","CROCKETT","GROVES","CLEMENT","LESLIE","CARTWRIGHT","DICKEY","MCGILL","DUBOIS","MUNIZ","ERWIN","SELF","TOLBERT","DEMPSEY","CISNEROS","SEWELL","LATHAM","GARLAND","VIGIL","TAPIA","STERLING","RAINEY","NORWOOD","LACY","STROUD","MEADE","AMOS","TIPTON","LORD","KUHN","HILLIARD","BONILLA","TEAGUE","COURTNEY","GUNN","HO","GREENWOOD","CORREA","REECE","WESTON","POE","TRENT","PINEDA","PHIPPS","FREY","KAISER","AMES","PAIGE","GUNTER","SCHMITT","MILLIGAN","ESPINOSA","CARLTON","BOWDEN","VICKERS","LOWRY","PRITCHARD","COSTELLO","PIPER","MCCLELLAN","LOVELL","DREW","SHEEHAN","QUICK","HATCH","DOBSON","SINGH","JEFFRIES","HOLLINGSWORTH","ORENSEN","MEZA","FINK","DONNELLY","BURRELL","BRUNO","TOMLINSON","COLBERT","BILLINGS","RITCHIE","HELTON","SUTHERLAND","PEOPLES","MCQUEEN","GASTON","THOMASON","MCKINLEY","GIVENS","CROCKER","VOGEL","ROBISON","DUNHAM","COKER","SWARTZ","KEYS","LILLY","LADNER","HANNAH","WILLARD","RICHTER","HARGROVE","EDMONDS","BRANTLEY","ALBRIGHT","MURDOCK","BOSWELL","MULLER","QUINTERO","PADGETT","KENNEY","DALY","CONNOLLY","PIERRE","INMAN","QUINTANA","LUND","BARNARD","VILLEGAS","SIMONS","LAND","HUGGINS","TIDWELL","SANDERSON","BULLARD","MCCLENDON","DUARTE","DRAPER","MEREDITH","MARRERO","DWYER","ABRAMS","STOVER","GOODE","FRASER","CREWS","BERNAL","SMILEY","GODWIN","FISH","CONKLIN","MCNEAL","BACA","ESPARZA","CROWDER","BOWER","NICHOLAS","CHUNG","BREWSTER","MCNEILL","DICK","RODRIGUES","LEAL","COATES","RAINES","MCCAIN","MCCORD","MINER","HOLBROOK","SWIFT","DUKES","CARLISLE","ALDRIDGE","ACKERMAN","STARKS","RICKS","HOLLIDAY","FERRIS","HAIRSTON","SHEFFIELD","LANGE","FOUNTAIN","MARINO","DOSS","BETTS","KAPLAN","CARMICHAEL","BLOOM","RUFFIN","PENN","KERN","BOWLES","SIZEMORE","LARKIN","DUPREE","JEWELL","SILVER","SEALS","METCALF","HUTCHISON","HENLEY","FARR","CASTLE","MCCAULEY","HANKINS","GUSTAFSON","DEAL","CURRAN","ASH","WADDELL","RAMEY","CATES","POLLOCK","MAJOR","IRVIN","CUMMINS","MESSER","HELLER","DEWITT","LIN","FUNK","CORNETT","PALACIOS","GALINDO","CANO","HATHAWAY","SINGER","PHAM","ENRIQUEZ","AARON","SALGADO","PELLETIER","PAINTER","WISEMAN","BLOUNT","HAND","FELICIANO","TEMPLE","HOUSER","DOHERTY","MEAD","MCGRAW","TONEY","SWAN","MELVIN","CAPPS","BLANCO","BLACKMON","WESLEY","THOMSON","MCMANUS","FAIR","BURKETT","POST","GLEASON","RUDOLPH","OTT","DICKENS","CORMIER","VOSS","RUSHING","ROSENBERG","HURD","DUMAS","BENITEZ","ARELLANO","STORY","MARIN","CAUDILL","BRAGG","JARAMILLO","HUERTA","GIPSON","COLVIN","BIGGS","VELA","PLATT","CASSIDY","TOMPKINS","MCCOLLUM","KAY","GABRIEL","DOLAN","DALEY","CRUMP","STREET","SNEED","KILGORE","GROVE","GRIMM","DAVISON","BRUNSON","PRATER","MARCUM","DEVINE","KYLE","DODGE","STRATTON","ROSAS","CHOI","TRIPP","LEDBETTER","LAY","HIGHTOWER","HAYWOOD","FELDMAN","EPPS","YEAGER","POSEY","SYLVESTER","SCRUGGS","COPE","STUBBS","RICHEY","OVERTON","TROTTER","SPRAGUE","CORDERO","BUTCHER","BURGER","STILES","BURGOS","WOODSON","HORNER","BASSETT","PURCELL","HASKINS","GEE","AKINS","ABRAHAM","HOYT","ZIEGLER","SPAULDING","HADLEY","GRUBBS","SUMNER","MURILLO","ZAVALA","SHOOK","LOCKWOOD","JARRETT","DRISCOLL","DAHL","THORPE","SHERIDAN","REDMOND","PUTNAM","MCWILLIAMS","MCRAE","CORNELL","FELTON","ROMANO","JOINER","SADLER","HEDRICK","HAGER","HAGEN","FITCH","COULTER","THACKER","MANSFIELD","LANGSTON","GUIDRY","FERREIRA","CORLEY","CONN","ROSSI","LACKEY","CODY","BAEZ","SAENZ","MCNAMARA","DARNELL","MICHEL","MCMULLEN","MCKENNA","MCDONOUGH","LINK","ENGEL","BROWNE","ROPER","PEACOCK","EUBANKS","DRUMMOND","STRINGER","PRITCHETT","PARHAM","MIMS","LANDERS","HAM","GRAYSON","STACY","SCHAFER","EGAN","TIMMONS","OHARA","KEEN","HAMLIN","FINN","CORTES","MCNAIR","LOUIS","CLIFFORD","NADEAU","MOSELEY","MICHAUD","ROSEN","OAKES","KURTZ","JEFFERS","CALLOWAY","BEAL","BAUTISTA","WINN","SUGGS","STERN","STAPLETON","LYLES","LAIRD","MONTANO","DIAMOND","DAWKINS","ROLAND","HAGAN","GOLDMAN","BRYSON","BARAJAS","LOVETT","SEGURA","METZ","LOCKETT","LANGFORD","HINSON","EASTMAN","ROCK","HOOKS","WOODY","SMALLWOOD","SHAPIRO","CROWELL","WHALEN","TRIPLETT","HOOKER","CHATMAN","ALDRICH","CAHILL","YOUNGBLOOD","YBARRA","STALLINGS","SHEETS","SAMUEL","REEDER","PERSON","PACK","LACEY","CONNELLY","BATEMAN","ABERNATHY","WINKLER","WILKES","MASTERS","HACKETT","GRANGER","GILLIS","SCHMITZ","SAPP","NAPIER","SOUZA","LANIER","GOMES","WEIR","OTERO","LEDFORD","BURROUGHS","BABCOCK","VENTURA","SIEGEL","DUGAN","CLINTON","CHRISTIE","BLEDSOE","ATWOOD","WRAY","VARNER","SPANGLER","OTTO","ANAYA","STALEY","KRAFT","FOURNIER","EDDY","BELANGER","WOLFF","THORNE","BYNUM","BURNETTE","BOYKIN","SWENSON","PURVIS","PINA","KHAN","DUVALL","DARBY","XIONG","KAUFFMAN","ALI","YU","HEALY","ENGLE","CORONA","BENOIT","VALLE","STEINER","SPICER","SHAVER","RANDLE","LUNDY","DOW","CHIN","CALVERT","STATON","NEFF","KEARNEY","DARDEN","OAKLEY","MEDEIROS","MCCRACKEN","CRENSHAW","BLOCK","BEAVER","PERDUE","DILL","WHITTAKER","TOBIN","CORNELIUS","WASHBURN","HOGUE","GOODRICH","EASLEY","BRAVO","DENNISON","VERA","SHIPLEY","KERNS","JORGENSEN","CRAIN","ABEL","VILLALOBOS","MAURER","LONGORIA","KEENE","COON","SIERRA","WITHERSPOON","STAPLES","PETTIT","KINCAID","EASON","MADRID","ECHOLS","LUSK","WU","STAHL","CURRIE","THAYER","SHULTZ","SHERWOOD","MCNALLY","SEAY","NORTH","MAHER","KENNY","HOPE","GAGNE","BARROW","NAVA","MYLES","MORELAND","HONEYCUTT","HEARN","DIGGS","CARON","WHITTEN","WESTBROOK","STOVALL","RAGLAND","QUEEN","MUNSON","MEIER","LOONEY","KIMBLE","JOLLY","HOBSON","LONDON","GODDARD","CULVER","BURR","PRESLEY","NEGRON","CONNELL","TOVAR","MARCUS","HUDDLESTON","HAMMER","ASHBY","SALTER","ROOT","PENDLETON","OLEARY","NICKERSON","MYRICK","JUDD","JACOBSEN","ELLIOT","BAIN","ADAIR","STARNES","SHELDON","MATOS","LIGHT","BUSBY","HERNDON","HANLEY","BELLAMY","JACK","DOTY","BARTLEY","YAZZIE","ROWELL","PARSON","GIFFORD","CULLEN","CHRISTIANSEN","BENAVIDES","BARNHART","TALBOT","MOCK","CRANDALL","CONNORS","BONDS","WHITT","GAGE","BERGMAN","ARREDONDO","ADDISON","MARION","LUJAN","DOWDY","JERNIGAN","HUYNH","BOUCHARD","DUTTON","RHOADES","OUELLETTE","KISER","RUBIN","HERRINGTON","HARE","DENNY","BLACKMAN","BABB","ALLRED","RUDD","PAULSON","OGDEN","KOENIG","JACOB","IRVING","GEIGER","BEGAY","PARRA","CHAMPION","LASSITER","HAWK","ESPOSITO","CHO","WALDRON","VERNON","RANSOM","PRATHER","KEENAN","JEAN","GROVER","CHACON","VICK","SANDS","ROARK","PARR","MAYBERRY","GREENBERG","COLEY","BRUNER","WHITMAN","SKAGGS","SHIPMAN","MEANS","LEARY","HUTTON","ROMO","MEDRANO","LADD","KRUSE","FRIEND","DARLING","ASKEW","VALENTIN","SCHULZ","ALFARO","TABOR","MOHR","GALLO","BERMUDEZ","PEREIRA","ISAAC","BLISS","REAVES","FLINT","COMER","BOSTON","WOODALL","NAQUIN","GUEVARA","EARL","DELONG","CARRIER","PICKENS","BRAND","TILLEY","SCHAFFER","READ","LIM","KNUTSON","FENTON","DORAN","CHU","VOGT","VANN","PRESCOTT","MCLAIN","LANDIS","CORCORAN","AMBROSE","ZAPATA","HYATT","HEMPHILL","FAULK","CALL","DOVE","BOUDREAUX","ARAGON","WHITLOCK","TREJO","TACKETT","SHEARER","SALDANA","HANKS","GOLD","DRIVER","MCKINNON","KOEHLER","CHAMPAGNE","BOURGEOIS","POOL","KEYES","GOODSON","FOOTE","EARLY","LUNSFORD","GOLDSMITH","FLOOD","WINSLOW","SAMS","REAGAN"};
    String[] roles = {"Administrative","Executive","Marketing","Customer Service","Financial","Software","Sales","Data Entry","Office","IT"};
    String[] functions = {"Assistant","Manager","Representative","Practitioner","Engineer","Clerk"};

}
