# JavaChatServer -- Similar to forums
Multi-threaded and thread-safe.
* User can comment on one topic.
* User can subscribe one topic to get realtime comment notification.
* User will get points asynchronously when he posts one comment (10% failure rate). You can check top 10 users.
* Rank will update every 1 mins.

Controll:

| Commands      | Discription      |
| ------------- |:-------------|
| /say       | Comment on one topic. If topic doesn't exist, system will create it for you. |
| /rank       | Check top 10 users. |
| /sub   [TOPIC]      | Subscribe one topic.      |
| /unsub [TOPIC] | UnSubscribe one topic.      |
| /read  [TOPIC]  | Read latest 10 comments from one topic.      |
| /help |     |
| /exit | quit this chat application      |

Usage:
Server Deploy:
```
Java -jar ChatApplication.jar
```
Client Connect:
```
Telnet SERVER_IP 8988
```

TODO:

* System will open a thread for every socket. Not so efficient.
  * Build epoll or mysql connection pool-like system.

* Use database.
