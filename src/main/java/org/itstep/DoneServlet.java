package org.itstep;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DoneServlet extends HttpServlet {

    private static File file = new File("e:\\JAVA\\Projects\\GitHub Homeworks\\Lesson070\\src\\main\\webapp\\resources\\db\\tasks.db");
    private static String path = "\\WEB-INF\\db\\tasks.db";
    private List<Task> tasks = new CopyOnWriteArrayList<>();

    public static String TEMPLATE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // 1. Загрузка TEMPLATE
        ServletContext servletContext = config.getServletContext();

        try (InputStream in = servletContext.getResourceAsStream("/WEB-INF/template/done.html");
             BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = rdr.readLine()) != null) {
                stringBuilder.append(line);
            }
            TEMPLATE = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeInFile(HttpServletRequest req, String path) throws MalformedURLException {
        ServletContext servletContext = req.getServletContext();
        URL url = servletContext.getResource(path);
        System.out.println("url = " + url);
        String pathWrite = url.toString().substring(6);
        System.out.println("pathWrite = " + pathWrite);
        // Запись в файл
        File fileWrite = new File(pathWrite);
        System.out.println("fileWrite.exists() = " + fileWrite.exists());
        if (fileWrite.exists()) {
            fileWrite.delete();
        }
        try (OutputStream out = new FileOutputStream(fileWrite);
             ObjectOutputStream objectOut = new ObjectOutputStream(out)) {
            objectOut.writeObject(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void readFromFile(HttpServletRequest req, String path) throws MalformedURLException {
        tasks = null;
        ServletContext servletContext = req.getServletContext();
        URL url = servletContext.getResource(path);
        System.out.println("url = " + url);
        String pathRead = url.toString().substring(6);
        System.out.println("pathRead = " + pathRead);
        File fileRead = new File(pathRead);
        System.out.println("fileRead.exists() = " + fileRead.exists());
        if (fileRead.exists()) {
            try (InputStream in = new FileInputStream(fileRead);
                 ObjectInputStream objectInput = new ObjectInputStream(in)) {
                tasks = (List<Task>) objectInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 2. Удаление
        String delete = req.getParameter("delete");
        System.out.println("delete = " + delete);
        if (delete != null && !delete.isBlank()) {
            // Считывание с файла
            readFromFile(req, path);
            // Удаление со списка
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(delete)) {
                    tasks.remove(tasks.get(i));
                }
            }
            // Запись в файл
            writeInFile(req, path);
        }


        // 3. Изменение condition (состояние)
        String condition = req.getParameter("condition");
        System.out.println("conditionDone = " + condition);
        if (condition != null && !condition.isBlank()) {
            // Считывание с файла
            readFromFile(req, path);
            // Изменение состояния на Active
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(condition)) {
                    tasks.get(i).setCondition(Condition.ACTIVE);
                }
            }
            // Запись в файл
            writeInFile(req, path);
        }


        // 4. Вывод HTML
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        // Считывание с файла
        readFromFile(req, path);
        System.out.println("DoneServlet");
        if (tasks != null) {
            tasks.stream().forEach(task -> {
                System.out.println(task.toString());
            });
        }
        // Отбор tasks соответствующих Done
        if (tasks != null) {
            List<Task> tasksDone = new CopyOnWriteArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getCondition().condition().equals("Done")) {
                    tasksDone.add(tasks.get(i));
                }
            }

            // Печать tasks соответствующих Done
            PrintWriter writer = resp.getWriter();
            String tasksDoneString = "<ul class='list'>" + tasksDone.stream().map(task -> "<li>" + task.toStringForDone() + "</li>").collect(Collectors.joining()) + "</ul>";
            System.out.println("tasksActiveString = " + tasksDoneString);
            writer.printf(TEMPLATE, tasksDoneString);
        }
    }


}



