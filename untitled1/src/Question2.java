import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;

class Question2 {
    public static String chars = null;
    public static String[] states = null;
    public static String startState = null;
    public static String[] endStates = null;
    public static HashMap<String, HashMap<String, String>> arcMap = new HashMap<>();
    public static HashMap<String, HashMap<String, String>> dfaArcMap = new HashMap<>();
    public static HashMap<String, String> map = new HashMap<>();

    public static void main(String[] args) {
        load();
        //start of conversion process
        conversion(nullClosure(startState));

        makeResult();

        save();
    }

    /**
     * This method will read NFA machine from file
     */
    private static void load(){
        try {
            File machineFile = new File("/home/salar/IdeaProjects/untitled1/Resrc/NFA_input_2.txt");
            Scanner fileReader = new Scanner(machineFile);
            chars = fileReader.nextLine();
            states = fileReader.nextLine().split(" ");
            startState = fileReader.nextLine();
            endStates = fileReader.nextLine().split(" ");
            //ین حلقه تا زمانی که فایل دارای خط بعدی باشد ادامه پیدا می‌کند.
            while(fileReader.hasNextLine()){
                String[] arc = fileReader.nextLine().split(" ");
                //اگر نقشه قبلاً کلید مشابه داشته باشد
                if(arcMap.containsKey(arc[0])){
                    //اگر یک کلید دیگر با نام مشابه وجود داشته باشد
                    if(arcMap.get(arc[0]).keySet().contains(arc[1])){
                        //اطلاعات جدید به اطلاعات قبلی اضافه می‌شوند.
                        arcMap.get(arc[0]).put(arc[1], arcMap.get(arc[0]).get(arc[1]) + " " + arc[2]);
                    } else {
                        //در غیر این صورت، یک کلید جدید ایجاد می‌شود و اطلاعات جدید به آن اضافه می‌شوند
                        arcMap.get(arc[0]).put(arc[1], arc[2]);
                    }
                } else {
                    //در صورتی که نقشه کلید مشابه نداشته باشد، یک کلید جدید ایجاد شده و اطلاعات جدید به آن اضافه می‌شوند.
                    arcMap.put(arc[0], new HashMap<String, String>());
                    arcMap.get(arc[0]).put(arc[1], arc[2]);
                }
            }
            fileReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * THis method will save converted NFA machine into file
     */
    private static void save(){
        try{
            // ایجاد یک فایل جدید برای ذخیره دستگاه DFA
            File file = new File("/home/salar/IdeaProjects/untitled1/Resrc/DFA_Output_2.txt");
            file.createNewFile();
            // ایجاد یک نویسنده فایل برای نوشتن در فایل
            FileWriter fileWriter = new FileWriter(file);
            // نوشتن علامات (characters) در فایل
            fileWriter.write(chars + "\n");
            // نوشتن نقشه‌ی نگاشت حالات تغییرنام‌دهی شده (map) در فایل
            for (String string : map.values()) {
                fileWriter.write(string + " ");
            }
            fileWriter.write("\n");
            // نوشتن حالت شروع تغییرنام‌دهی شده در فایل
            fileWriter.write(map.get(nullClosure(startState)) + "\n");
            // پیدا کردن حالات پایانی تغییرنام‌دهی شده و نوشتن آن‌ها در فایل
            String dfaEnds = "";

            for (String ends : endStates) {
                for (String state : map.keySet()) {
                    if(state.contains(ends) && !dfaEnds.contains(state)){
                        dfaEnds = dfaEnds + " " + map.get(state);
                    }
                }
            }

            fileWriter.write(dfaEnds.trim() + "\n");
            // نوشتن یال‌ها (transitions) در فایل
            for (String state : dfaArcMap.keySet()) {
                for (String head : chars.split(" ")) {
                    if(!isNullOrEmpty(map.get(dfaArcMap.get(state).get(head)))){
                        fileWriter.write(map.get(state) + " " + head + " " + map.get(dfaArcMap.get(state).get(head)) + "\n");
                    }
                }
            }
            // بستن نویسنده فایل
            fileWriter.close();
        } catch (Exception e){
            // چاپ خطاها در صورت وجود خطا
            e.printStackTrace();
        }
    }

    /**
     * This method will make a hashmap of renamed state on DFA machine
     */
    private static void makeResult(){
        int count = 0;
        for (String state : dfaArcMap.keySet()) {
            // اگر حالت خالی نباشد و هنوز در نگاشت map وجود نداشته باشد، یک نگاشت جدید ایجاد می‌شود.
            if(!isNullOrEmpty(state) && !map.containsKey(state)){
                map.put(state, "s" + count);
                count++;
            }
            // برای هر علامت از chars
            for (String head : chars.split(" ")) {
                boolean isRepeative = false;
                // بررسی می‌شود که آیا حالت مقصد در نگاشت map وجود دارد یا نه
                for (String subState : map.keySet()) {
                    if(equalState(subState, dfaArcMap.get(state).get(head))){
                        // اگر حالت مقصد در نگاشت map وجود داشته باشد، علامت جدید با نام حالت مقصد جایگزین می‌شود.
                        dfaArcMap.get(state).replace(head, subState);
                        isRepeative = true;
                        break;
                    }
                }
                // اگر حالت مقصد در نگاشت map وجود نداشته باشد
                if(!isRepeative){
                    // اگر حالت مقصد خالی نباشد، یک نگاشت جدید با نام "s" + count ایجاد می‌شود و مقدار count افزایش می‌یابد.
                    if(!isNullOrEmpty(dfaArcMap.get(state).get(head))){
                        map.put(dfaArcMap.get(state).get(head), "s" + count);
                        count++;
                    }
                }
            }
        }
    }

    /**
     * Return transitions of a specific state with a head
     */
    private static String arcFinder(String state, String readHead){
        String finalStr = "";
        for (String subState : state.split(" ")) {
            if(arcMap.keySet().contains(subState)){
                //اگر نقشه مرتبط با subState شامل کلید readHead باشد:
                if(arcMap.get(subState).keySet().contains(readHead)){

                    //حالت‌هایی که با یال readHead به subState متصل هستند با حالت‌های قبلی (finalStr) ترکیب می‌شوند.
                    finalStr = unionStates(finalStr, arcMap.get(subState).get(readHead));
                } else {
                    finalStr = unionStates(finalStr, "");
                }
            } else {
                finalStr = unionStates(finalStr, "");
            }
        }
        return finalStr.trim();
    }

    /**
     * Returns nullClosure of a state
     * @param state
     * @return
     */
    private static String nullClosure(String state){
        String nullCloString = state;
        for (String subState : state.split(" ")) {
            //اگر نقشه (arcMap) شامل کلید مشابه subState باشد:
            if(arcMap.keySet().contains(subState)){
                //اگر نقشه مرتبط با subState شامل کلید "~" باشد:
                if(arcMap.get(subState).keySet().contains("~")){
                    //از متد nullClosure برای یافتن حالت‌هایی که با "~" متصل شده‌اند، استفاده می‌شود.
                    //این حالت‌ها با حالت اصلی ترکیب شده و مقدار nullCloString به‌روزرسانی می‌شود.
                    nullCloString = unionStates(nullCloString, nullClosure(arcMap.get(subState).get("~")));
                }
            }
        }
        //trim() نیز برای حذف فاصله‌های اضافی از دو طرف رشته استفاده شده است.
        return nullCloString.trim();
    }

    /**
     * Returns a union of two state in string format
     * @param firstStr
     * @param secondStr
     * @return
     */
    private static String unionStates(String firstStr, String secondStr){
        firstStr = firstStr.trim();
        secondStr = secondStr.trim();

        String finalStr = "";
        String[] firstArr = firstStr.split(" ");
        String[] secondArr = secondStr.split(" ");

        for(int i = 0; i < firstArr.length; i++){
            //اگر زیرحالت فعلی در finalStr وجود نداشته باشد، به finalStr اضافه می‌شود.
            if(!finalStr.contains(firstArr[i])){
                finalStr = finalStr + " " + firstArr[i];
            }
        }

        for(int i = 0; i < secondArr.length; i++){
            //اگر زیرحالت فعلی در finalStr وجود نداشته باشد، به finalStr اضافه می‌شود
            if(!finalStr.contains(secondArr[i])){
                finalStr = finalStr + " " + secondArr[i];
            }
        }

        return finalStr.trim();
    }

    /**
     * This method is main method of NFA to DFA convert
     * @param state
     */
    private static void conversion(String state){
        //یک ورودی جدید به نقشه (dfaArcMap) اضافه می‌شود که به آن حالت (state) مرتبط با یک نقشه جدید از یال‌ها اشاره دارد.
        dfaArcMap.put(state, new HashMap<String, String>());
        for (String head : chars.split(" ")) {
            String target = nullClosure(arcFinder(state, head));

            if(!isNullOrEmpty(target)){
                //یک یال با نشان head از حالت state به حالت target در نقشه dfaArcMap افزوده می‌شود.
                dfaArcMap.get(state).put(head, target);
                boolean isRepeative = false;
                for (String subState : dfaArcMap.keySet()) {
                    if(equalState(subState, target)){
                        isRepeative = true;
                        break;
                    }
                }
                //این کار تا زمانی ادامه می‌یابد که برای هر یک از حروف head تمام حالت‌های متصل شده به آن حالت به صورت بازگشتی پردازش شوند.
                if(!isRepeative){
                    conversion(target);
                }
            }
        }
    }

    /**
     * This method will check two states to show if they are equal or not
     * @param firstStr
     * @param secStr
     * @return true for equality and false for inequality
     */
    private static boolean equalState(String firstStr, String secStr){
        String[] firstArr = firstStr.split(" ");
        String[] secArr = null;
        //یک آرایه از زیرحالت‌های secStr نیز با استفاده از split(" ") ایجاد می‌شود. این قسمت در یک try-catch قرار گرفته است تا اگر secStr null باشد، از ارور NullPointerException جلوگیری شود و مقدار false به عنوان خروجی بازگردانده شود.
        try{
            secArr = secStr.split(" ");
        } catch (NullPointerException e){
            return false;
        }

        ///اگر تعداد زیرحالت‌های firstStr و secStr برابر نباشد، مقدار false به عنوان خروجی بازگردانده می‌شود. این بررسی انجام می‌شود تا اطمینان حاصل شود که دو حالت تعداد یکسانی از زیرحالت‌ها دارند.
        //اگر یک زیرحالت در firstArr در secStr وجود نداشته باشد، مقدار false به عنوان خروجی بازگردانده می‌شود. این بررسی نشان‌دهنده عدم برابری دو حالت است.
        if(firstArr.length != secArr.length){
            return false;
        } else {
            for (String state : firstArr) {
                if(!secStr.contains(state)){
                    return false;
                }
            }
            return true;
        }
    }

//در این قسمت، یک شرط if استفاده شده است.
//ابتدا بررسی می‌شود که str متغیری غیر null باشد (str != null).
//سپس بررسی می‌شود که str خالی (empty) نباشد (!str.isEmpty()).
//اگر هر دو شرط برقرار باشند، یعنی str یا null نیست و همچنین خالی نیست، مقدار false به عنوان خروجی باز می‌گرداند.
    private static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
}



















//البته، معذرت می‌خواهم اگر توضیحات پیچیده به نظر آمده باشد. بیایید این را از ابتدا و با استفاده از مثال ساده‌تری توضیح دهم:
//
//فرض کنید می‌خواهیم تمام حالت‌هایی که از یک حالت خاص به وسیله یال "~" (تقریباً به معنی عدم حرکت) متصل هستند را بیابیم. برای این کار از یک تابع به نام nullClosure استفاده می‌کنیم.
//
//حالت nullCloString ابتدا با حالت ورودی state مقداردهی می‌شود. سپس یک حلقه for بر روی زیرحالت‌های state ایجاد می‌شود. در هر دور از حلقه:
//
//اگر نقشه شامل کلید مشابه subState باشد (یعنی حالت subState وجود داشته باشد):
//اگر نقشه مرتبط با subState شامل کلید "~" باشد:
//از تابع nullClosure برای یافتن حالت‌هایی که با "~" متصل شده‌اند، استفاده می‌شود.
//حالت‌های جدید با حالت اصلی ترکیب شده و مقدار nullCloString به‌روزرسانی می‌شود.
//در اینجا unionStates نیز یک تابع است که می‌پذیرد دو رشته حالت و این دو را به یکدیگر اضافه می‌کند و حالت‌های تکراری را حذف می‌کند. اینجا از این تابع برای ادغام حالت‌هایی که از "~" به دست آمده‌اند و حالت‌های موجود در nullCloString استفاده شده است.
//
//به این ترتیب، nullCloString به مرور زمان حاصل از ترکیب تمام حالت‌های مستقیم یا غیرمستقیم از یال "~" متصل به حالت ورودی state می‌شود. این حالت‌ها به عنوان خروجی تابع nullClosure بازگردانده می‌شوند.