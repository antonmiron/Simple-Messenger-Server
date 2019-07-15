package com.example.mymessenger;


import com.example.mymessenger.models.Message;
import com.example.mymessenger.network.Connection;
import com.example.mymessenger.tools.ConsoleHelper;
import com.example.mymessenger.tools.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    // ключ -имя клиента, а значение - соединение с ним
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        ConsoleHelper.writeMessage("Введите порт сервера: ");
        int serverPort = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            ConsoleHelper.writeMessage("Сервер запущен");

            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        }

    }


    /** отправка сообщения для всех **/
    public static void sendBroadcastMessage(Message message) {

        try {
            ConsoleHelper.writeMessage(message.getData());
            for (Connection connection : connectionMap.values()) {
                connection.send(message);
            }

        } catch (Exception e){
            e.printStackTrace();
            ConsoleHelper.writeMessage("Сообщение не отправлено");
        }

    }


    /**обработчик Handler, в котором будет происходить обмен сообщениями с клиентом **/
    private static class Handler extends Thread {

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {

            ConsoleHelper.writeMessage("Установленно соединение с адресом " + socket.getRemoteSocketAddress());
            String clientName = null;
            try (Connection connection = new Connection(socket)) {
                ConsoleHelper.writeMessage("Подключение к порту: " + connection.getRemoteSocketAddress());
                clientName = serverHandshake(connection);
                //Рассылать всем участникам чата информацию об имени присоединившегося участника (сообщение с типом USER_ADDED)
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                sendListOfUsers(connection, clientName);

                serverMainLoop(connection, clientName);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Ошибка при обмене данными с удаленным адресом");
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка при обмене данными с удаленным адресом");
            }

            //если участник вышел
            connectionMap.remove(clientName);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, clientName));

            ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто");
        }

        /**"Знакомимся" с клиентом**/
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();

                if (message.getType() == MessageType.USER_NAME) {
                    if (message.getData() != null && !message.getData().isEmpty()) {
                        //пользователь с таким именем еще не подключен
                        if (connectionMap.get(message.getData()) == null) {
                            connectionMap.put(message.getData(), connection);
                            // Отправить клиенту команду информирующую, что его имя принято
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return message.getData();
                        }
                        //иначе сообщаем об этом
                        else
                            connection.send(new Message(MessageType.NAME_NOT_ACCEPTED));
                    }
                }
            }
        }


        /** Отправка списка всех пользователей **/
        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String key : connectionMap.keySet()) {
                Message message = new Message(MessageType.USER_ADDED, key);

                if (!key.equals(userName)) {
                    connection.send(message);
                }
            }
        }


        /** Главный цикл обработки сообщений сервером **/
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String s = userName + ": " + message.getData();

                    Message formattedMessage = new Message(MessageType.TEXT, s);
                    sendBroadcastMessage(formattedMessage);
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }
    }
}
