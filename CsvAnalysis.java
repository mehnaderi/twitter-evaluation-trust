import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvAnalysis {

    private static String[][] users;
    private static String[][] friends;
    private static String[][] mentioned;
    private static String[][] tweetsRelation;
    private static String[][] tweetsTable;
    private static User[] userTweetList = new User[14894];
    //        221,816,342
//    private static String[][] recordsArray = new String[221816342][8];
    public static void main(String[] args) {
        for(int i=0; i<userTweetList.length;i++)
        {
            User user = new User();
            userTweetList[i] = user;
        }
        File myFile = new File("C:\\Users\\Mehrdad-PC\\Desktop\\filename.csv");
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(myFile);
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



        String csvFileUsers = "C:\\Users\\Mehrdad-PC\\Desktop\\TW\\tas_users.csv";
        String csvFileFriends = "C:\\Users\\Mehrdad-PC\\Desktop\\TW\\twitter_tas_follower_friend_index.csv";
        String csvFileMentioned = "C:\\Users\\Mehrdad-PC\\Desktop\\TW\\twitter_user_mention_index.csv";
        String csvFileRelation = "C:\\Users\\Mehrdad-PC\\Desktop\\TW\\twitter_tweets_relation_index.csv";
        String csvFileTweets = "C:\\Users\\Mehrdad-PC\\Desktop\\TW\\twitter_tweets_all_user_posted.csv";
        users = readCsvToArray(csvFileUsers, 2, 14894);
        friends = readCsvToArray(csvFileFriends, 2, 412475);
        mentioned = readCsvToArray(csvFileMentioned, 2, 459938);
        tweetsRelation = readCsvToArray(csvFileRelation, 3, 428582);
        tweetsTable = readCsvToArray(csvFileTweets, 3, 8401895);



        for (int i = 0; i < users[0].length; i++) {
            System.out.println("User : "+i);
            for (int j = 0; j < users[0].length; j++) {
                if(i!=j)
                {
                    System.out.println("User : "+i+" :: second user : " + j);
                    Record record = new Record();
                    record.setUser1(users[0][i]);
                    record.setUser2(users[0][j]);
//                    System.out.println("User : "+i+"  ::  find Follows  ");
                    boolean[] follows = findFollows(users[0][i], users[0][j]);
                    record.setOne_Follows_two(follows[0]?"1":"0");
                    record.setTwo_Follows_one(follows[1]?"1":"0");
//                    System.out.println("User : "+i+"  ::  find Relations  ");
                    if(follows[0] || follows[1])
                    {
                        int[] rel = findRel(i, j);
                        record.setNumberOfMentions(String.valueOf(rel[0]));
                        record.setNumberOfReplies(String.valueOf(rel[1]));
                        record.setNumberOfReTweets(String.valueOf(rel[2]));
//                    record.setNumberOfTweets(String.valueOf(rel[3]));

                        try {
                            myWriter.write(record.getUser1() + "," + record.getUser2() + "," + record.getOne_Follows_two()
                                    + "," + record.getTwo_Follows_one()+ "," + record.getNumberOfMentions()+ ","
                                    + record.getNumberOfReplies() + "," + record.getNumberOfReTweets()+"\n");
                            System.out.println(record.getUser1() + "," + record.getUser2() + "," + record.getOne_Follows_two()
                                    + "," + record.getTwo_Follows_one()+ "," + record.getNumberOfMentions()+ ","
                                    + record.getNumberOfReplies() + "," + record.getNumberOfReTweets()+"\n");
                            myWriter.flush();
                        } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                    }

//                    records.add(record);
                }
            }

        }

        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean[] findFollows(String user1, String user2) {
        boolean oneFollowsTwo = false, twoFollowsOne = false;
        for(int i=0; i<friends[0].length; i++)
        {
            if( !oneFollowsTwo && friends[0][i].equals(user1) && friends[1][i].equals(user2))
            {
                oneFollowsTwo = true;
            }
            if( !twoFollowsOne && friends[0][i].equals(user2) && friends[1][i].equals(user1))
            {
                twoFollowsOne = true;
            }
            if(oneFollowsTwo && twoFollowsOne)
            {
                break;
            }
        }
        return new boolean[]{oneFollowsTwo, twoFollowsOne};
    }

    private static int[] findRel(int lineUser1, int lineUser2)
    {
        int mentionN = 0;
        int replyN = 0;
        int reTweetN = 0;

        List<Integer> user1TweetList = new ArrayList<>();
        List<Integer> user2TweetList = new ArrayList<>();

        if(userTweetList[lineUser1].isCompleted())
        {
            user1TweetList = userTweetList[lineUser1].getTweet_lines();
        }
        if(userTweetList[lineUser2].isCompleted())
        {
            user2TweetList = userTweetList[lineUser2].getTweet_lines();
        }
        if(!userTweetList[lineUser1].isCompleted() || !userTweetList[lineUser2].isCompleted())
        {
            updateUserTweetList(lineUser1, lineUser2);
            user1TweetList = userTweetList[lineUser1].getTweet_lines();
            user2TweetList = userTweetList[lineUser2].getTweet_lines();
        }

        List<Integer> user1RelLineList = new ArrayList<>();
        if(userTweetList[lineUser1].isRelCompleted()){
            user1RelLineList = userTweetList[lineUser1].getRel_lines();
        }

        if(!userTweetList[lineUser1].isRelCompleted() || !userTweetList[lineUser2].isRelCompleted())
        {
            updateUserRelList(lineUser1, lineUser2, user1TweetList, user2TweetList);
            user1RelLineList = userTweetList[lineUser1].getRel_lines();
        }

        for (Integer integer : user1RelLineList) {
            for (int i = 0; i < user2TweetList.size(); i++){

                if (tweetsRelation[1][integer].equals(tweetsTable[1][user2TweetList.get(i)])) {
                    if (tweetsRelation[2][integer].equals("\"1\"")) {
                        System.out.println("reply:           "+tweetsRelation[2][integer]);
                        replyN++;
                    } else {
                        System.out.println("reTweet:           "+tweetsRelation[2][integer]);
                        reTweetN++;
                    }
                }

            }
        }

        if(userTweetList[lineUser1].isMenCompleted())
        {
            for (int i = 0; i < userTweetList[lineUser1].getMention_lines().size(); i++) {
                if(mentioned[0][userTweetList[lineUser1].getMention_lines().get(i)].equals(users[0][lineUser2]))
                {
                    mentionN++;
                }
            }
        }else
        {
            List<Integer> user1List = new ArrayList<>();
            for (Integer integer : user1TweetList) {
                for (int j = 0; j < mentioned[0].length; j++) {

                    if (mentioned[1][j].equals(tweetsTable[1][integer])) {
                        user1List.add(j);
                    }
                }
            }
            userTweetList[lineUser1].setMenCompleted(true);
            userTweetList[lineUser1].setMention_lines(user1List);
            for (int i = 0; i < userTweetList[lineUser1].getMention_lines().size(); i++) {
                if(mentioned[0][userTweetList[lineUser1].getMention_lines().get(i)].equals(users[0][lineUser2]))
                {
                    mentionN++;
                }
            }
        }


//                if(findMentionInner(tweetsTable[1][i], user2))
//                {
//                    count++;
//                }


        return new int[]{mentionN, replyN, reTweetN};
    }

    private static void updateUserRelList(int lineUser1, int lineUser2, List<Integer> user1TweetList, List<Integer> user2TweetList) {
        boolean isCompleted1 = userTweetList[lineUser1].isRelCompleted();
        boolean isCompleted2 = userTweetList[lineUser2].isRelCompleted();

        if(!isCompleted1 && !isCompleted2)
        {
            List<Integer> user1List = new ArrayList<>();
            List<Integer> user2List = new ArrayList<>();
            for (int i = 0; i < tweetsRelation[2].length; i++)
            {
                boolean isFirst = false;
                for (int j = 0; j < user1TweetList.size(); j++) {
                    if(tweetsRelation[0][i].equals(tweetsTable[1][user1TweetList.get(j)]))
                    {
                        user1List.add(i);
                        isFirst = true;
                        break;
                    }
                }
                if(!isFirst)
                {
                    for (int j = 0; j < user2TweetList.size(); j++) {
                        if(tweetsRelation[0][i].equals(tweetsTable[1][user2TweetList.get(j)]))
                        {
                            user2List.add(i);
                            break;
                        }
                    }
                }
            }
            userTweetList[lineUser1].setRel_lines(user1List);
            userTweetList[lineUser2].setRel_lines(user2List);
            userTweetList[lineUser1].setRelCompleted(true);
            userTweetList[lineUser2].setRelCompleted(true);

        }
        else if(!isCompleted1)
        {
            List<Integer> user1List = new ArrayList<>();
            for (int i = 0; i < tweetsRelation[2].length; i++)
            {
                for (Integer integer : user1TweetList) {
                    if (tweetsRelation[0][i].equals(tweetsTable[1][integer])) {
                        user1List.add(i);
                        break;
                    }
                }
            }
            userTweetList[lineUser1].setRel_lines(user1List);
            userTweetList[lineUser1].setRelCompleted(true);
        }else
        {
            List<Integer> user2List = new ArrayList<>();
            for (int i = 0; i < tweetsRelation[2].length; i++)
            {
                for (int j = 0; j < user2TweetList.size(); j++) {
                    if(tweetsRelation[0][i].equals(tweetsTable[1][user2TweetList.get(j)]))
                    {
                        user2List.add(i);
                        break;
                    }
                }
            }
            userTweetList[lineUser2].setRel_lines(user2List);
            userTweetList[lineUser2].setRelCompleted(true);
        }

    }

    private static void updateUserTweetList(int lineUser1, int lineUser2) {
        boolean isCompleted1 = userTweetList[lineUser1].isCompleted();
        boolean isCompleted2 = userTweetList[lineUser2].isCompleted();


        if(!isCompleted1 && !isCompleted2)
        {
            List<Integer> user1List = new ArrayList<>();
            List<Integer> user2List = new ArrayList<>();
            for (int i = 0; i < tweetsTable[2].length; i++) {
                if(tweetsTable[2][i].equals(users[0][lineUser1]))
                {
                    user1List.add(i);
                }
                else if(tweetsTable[2][i].equals(users[0][lineUser2]))
                {
                    user2List.add(i);
                }
            }
            userTweetList[lineUser1].setTweet_lines(user1List);
            userTweetList[lineUser2].setTweet_lines(user2List);
            userTweetList[lineUser1].setCompleted(true);
            userTweetList[lineUser2].setCompleted(true);

        }
        else if(!isCompleted1)
        {
            List<Integer> user1List = new ArrayList<>();
            for (int i = 0; i < tweetsTable[2].length; i++) {
                if(tweetsTable[2][i].equals(users[0][lineUser1]))
                {
                    user1List.add(i);
                }
            }
            userTweetList[lineUser1].setTweet_lines(user1List);
            userTweetList[lineUser1].setCompleted(true);
        }else
        {
            List<Integer> user2List = new ArrayList<>();
            for (int i = 0; i < tweetsTable[2].length; i++) {
                if(tweetsTable[2][i].equals(users[0][lineUser2]))
                {
                    user2List.add(i);
                }
            }
            userTweetList[lineUser2].setTweet_lines(user2List);
            userTweetList[lineUser2].setCompleted(true);
        }
    }


//    private static int[] findNumberOfMentions(String user1, String user2) {
//        int count = 0;
//        int reply = 0;
//        int reTweet = 0;
//
//        List<TweetR> user1TweetList = new ArrayList<>();
//        List<TweetR> user2TweetList = new ArrayList<>();
//
//
//        //        System.out.println("find Relations ::: step1 ");
//        for (int i = 0; i < tweetsTable[2].length; i++) {
//            if(tweetsTable[2][i].equals(user1))
//            {
//                TweetR tweet = new TweetR(tweetsTable[1][i], tweetsTable[2][i], i);
//                user1TweetList.add(tweet);
//                if(findMentionInner(tweetsTable[1][i], user2))
//                {
//                    count++;
//                }
//            }
//            else if(tweetsTable[2][i].equals(user2))
//            {
//                TweetR tweet = new TweetR(tweetsTable[1][i], tweetsTable[2][i], i);
//                user2TweetList.add(tweet);
//            }
//        }
////        System.out.println("find Relations ::: step2 ");
//        for (TweetR tweetUser1 : user1TweetList) {
//            for (int j = 0; j < tweetsRelation[0].length; j++) {
//
//                if (tweetsRelation[0][j].equals(tweetUser1)) {
//                    for (TweetR tweetR : user2TweetList) {
//
//                        if (tweetsRelation[1][j].equals(tweetR.tweet_index)) {
//                            if (tweetsRelation[2][j].equals("1")) //reply
//                            {
//                                reply++;
//                            } else //reTweet
//                            {
//                                reTweet++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//
//
//        return new int[]{count, reply, reTweet, user2TweetList.size()};
//    }

    private static boolean findMentionInner(String tweetIndex, String user2) {
        for (int i = 0; i < mentioned[1].length; i++) {
            if(mentioned[1][i].equals(tweetIndex) && mentioned[0][i].equals(user2))
            {
                return true;
            }
        }
        return false;
    }

    private static String[][] readCsvToArray(String csvFile, int numberOfAttrs, int numberOfRecords)
    {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        String[][] resultArray = new String[numberOfAttrs][numberOfRecords];
        int counter = 0;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] oneUser = line.split(cvsSplitBy);
                for(int i=0; i<numberOfAttrs; i++)
                {
                    resultArray[i][counter] = oneUser[i];
                }
                counter++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultArray;
    }



    static class Record
    {
        private String user1;
        private String user2;
        private String one_Follows_two;
        private String two_Follows_one;
        private String numberOfMentions; // number of times that user1 is mentioned user2
        private String numberOfTweets; // number of tweets for user2
        private String numberOfReTweets; // number of times that user1 is retweeted user2's tweets
        private String numberOfReplies; // number of replies that user2 is replied to user2's tweets


        public String getUser1() {
            return user1;
        }

        public void setUser1(String user1) {
            this.user1 = user1;
        }

        public String getUser2() {
            return user2;
        }

        public void setUser2(String user2) {
            this.user2 = user2;
        }

        public String getOne_Follows_two() {
            return one_Follows_two;
        }

        public void setOne_Follows_two(String one_Follows_two) {
            this.one_Follows_two = one_Follows_two;
        }

        public String getTwo_Follows_one() {
            return two_Follows_one;
        }

        public void setTwo_Follows_one(String two_Follows_one) {
            this.two_Follows_one = two_Follows_one;
        }

        public String getNumberOfMentions() {
            return numberOfMentions;
        }

        public void setNumberOfMentions(String numberOfMentions) {
            this.numberOfMentions = numberOfMentions;
        }

        public String getNumberOfTweets() {
            return numberOfTweets;
        }

        public void setNumberOfTweets(String numberOfTweets) {
            this.numberOfTweets = numberOfTweets;
        }

        public String getNumberOfReTweets() {
            return numberOfReTweets;
        }

        public void setNumberOfReTweets(String numberOfReTweets) {
            this.numberOfReTweets = numberOfReTweets;
        }

        public String getNumberOfReplies() {
            return numberOfReplies;
        }

        public void setNumberOfReplies(String numberOfReplies) {
            this.numberOfReplies = numberOfReplies;
        }
    }
    static class User
    {
        private String user_index;
        private List<Integer> tweet_lines;
        private boolean isCompleted = false;
        private List<Integer> rel_lines;
        private boolean isRelCompleted = false;
        private List<Integer> mention_lines;
        private boolean isMenCompleted = false;


        public String getUser_index() {
            return user_index;
        }

        public void setUser_index(String user_index) {
            this.user_index = user_index;
        }

        public List<Integer> getTweet_lines() {
            return tweet_lines;
        }

        public void setTweet_lines(List<Integer> tweet_lines) {
            this.tweet_lines = tweet_lines;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }

        public List<Integer> getRel_lines() {
            return rel_lines;
        }

        public void setRel_lines(List<Integer> rel_lines) {
            this.rel_lines = rel_lines;
        }

        public boolean isRelCompleted() {
            return isRelCompleted;
        }

        public void setRelCompleted(boolean relCompleted) {
            isRelCompleted = relCompleted;
        }

        public List<Integer> getMention_lines() {
            return mention_lines;
        }

        public void setMention_lines(List<Integer> mention_lines) {
            this.mention_lines = mention_lines;
        }

        public boolean isMenCompleted() {
            return isMenCompleted;
        }

        public void setMenCompleted(boolean menCompleted) {
            isMenCompleted = menCompleted;
        }
    }
}
