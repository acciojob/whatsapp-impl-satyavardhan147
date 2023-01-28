package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    private HashMap<Integer, Message> messageMap;
    private HashMap<String, User> userMap;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
        this.messageMap = new HashMap<>();
    }

    public String createUser(String name, String mobile) throws RuntimeException{
        if(userMap.containsKey(mobile)) {
            throw new RuntimeException("User already exists");
        } else {
            userMap.put(mobile, new User(name, mobile));
        }

        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        Group group;
        if(users.size() == 2){
            String name = users.get(1).getName();
            group = new Group(name, users.size());
            adminMap.put(group, users.get(0));
        }else{
            customGroupCount++;
            String name = "Group " + customGroupCount;
            group = new Group(name, users.size());
            adminMap.put(group, users.get(0));
        }
        groupUserMap.put(group, users);
        groupMessageMap.put(group, new ArrayList<>());
        return group;
    }

    public int createMessage(String content){
        messageId++;
        Message message = new Message(messageId, content, new Date());
        messageMap.put(messageId, message);
        return messageId;
    }

    public int sendMessage(Message message, User user, Group group) throws RuntimeException {
        if(!groupUserMap.containsKey(group)){
            throw new RuntimeException("Group does not exist");
        }
        if(!groupUserMap.get(group).contains(user)){
            throw new RuntimeException("You are not allowed to send message");
        }
        groupMessageMap.get(group).add(message);
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws RuntimeException {
        if(!groupUserMap.containsKey(group)){
            throw new RuntimeException("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new RuntimeException("Approver does not have rights");
        }
        if(!groupUserMap.get(group).contains(user)){
            throw new RuntimeException("User is not a participant");
        }
        adminMap.put(group, user);
        return "SUCCESS";
    }
}
