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
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

//@WebServlet(name = "home", urlPatterns = "/")
public class HomeServlet extends HttpServlet {
    private static SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static File file = new File("e:\\JAVA\\Projects\\GitHub Homeworks\\Lesson070\\src\\main\\webapp\\WEB-INF\\db\\tasks.db");

    private static String path = "\\WEB-INF\\db\\tasks.db";

    private List<Task> tasks = new CopyOnWriteArrayList<>();

    public static File fileDB;

    public static String TEMPLATE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // 1. Загрузка TEMPLATE
        ServletContext servletContext = config.getServletContext();
        try (var in = servletContext.getResourceAsStream("/WEB-INF/template/home.html");
             var rdr = new BufferedReader(new InputStreamReader(in))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = rdr.readLine()) != null) {
                stringBuilder.append(line);
            }
            TEMPLATE = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 2. Формирование БД с записью в файл
        try {
            tasks.add(new Task("Buy milk", "1 bottle", Category.SHOPPING, "2023-04-10", Priority.NORMAL));
            tasks.add(new Task("Made something", "For small daughter", Category.HOUSE, "2023-05-12", Priority.LOW));
            tasks.add(new Task("Take something", "For Jake", Category.WORK, "2023-06-08", Priority.LOW));
            tasks.add(new Task("Training", "Pay for the lesson", Category.FITNESS, "2023-05-10", Priority.HIGH));
            tasks.add(new Task("Buy beer", "1 bottle", Category.SHOPPING, "2023-04-18", Priority.NORMAL));
            tasks.add(new Task("Training", "Take running shoes and sports uniform", Category.FITNESS, "2023-05-20", Priority.HIGH));
            tasks.add(new Task("Take something", "For Ann", Category.WORK, "2023-10-10", Priority.HIGH));
            tasks.add(new Task("Buy bread", "1 bread loaf", Category.SHOPPING, "2023-04-14", Priority.NORMAL));
            tasks.add(new Task("Happy birthday", "Present for husband", Category.HOUSE, "2023-07-10", Priority.HIGH));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Запись в файл
        writeInFile(file);
    }

    private void writeInFile(File file) {
        // Запись в файл
        if (file.exists()) {
            file.delete();
        }
        try (OutputStream out = new FileOutputStream(file);
             ObjectOutputStream objectOut = new ObjectOutputStream(out)) {
            objectOut.writeObject(tasks);
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

//        try (var in = servletContext.getResourceAsStream(path);           // не работает
//             ObjectInputStream objectInput = new ObjectInputStream(in)) {
//            tasks = (List<Task>) objectInput.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
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

        // 3. Удаление
        String delete = req.getParameter("delete");
        System.out.println("delete = " + delete);
        if (delete != null && !delete.isBlank()) {
            // Считывание с файла
            readFromFile(req, path);
            // Удаление со списка
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(delete)) {
                    System.out.println("delete tsk with id = " + tasks.get(i).getId());
                    tasks.remove(tasks.get(i));

                }
            }
            System.out.println("HomeServlet: after delete");
            if (tasks != null) {
                tasks.stream().forEach(task -> {
                    System.out.println(task.toString());
                });
            }
            // Запись в файл
            writeInFile(req, path);
        }

        // 4. Изменение condition (состояние)
        String condition = req.getParameter("condition");
        System.out.println("conditionHome = " + condition);
        if (condition != null && !condition.isBlank()) {
            // Считывание с файла
            readFromFile(req, path);
            // Изменение состояния на Done
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(condition)) {
                    tasks.get(i).setCondition(Condition.DONE);
                }
            }
            // Запись в файл
            writeInFile(req, path);
        }


        // 5. Вывод HTML
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        // Считывание с файла
        readFromFile(req, path);

        System.out.println("HomeServlet");
        if (tasks != null) {
            tasks.stream().forEach(task -> {
                System.out.println(task.toString());
            });
        }

        // Отбор tasks соответствующих Active
        List<Task> tasksActive = new CopyOnWriteArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getCondition().condition().equals("Active")) {
                tasksActive.add(tasks.get(i));
            }
        }
        // Печать tasks соответствующих Active
        PrintWriter writer = resp.getWriter();
        String tasksActiveString = "<ul class='list'>" +
                                   tasksActive.stream().map(task ->
                                                   "<li>" + task.toStringForHome() + "</li>")
                                           .collect(Collectors.joining())
                                   + "</ul>";
//        System.out.println("tasksActiveString = " + tasksActiveString);
        writer.printf(TEMPLATE, tasksActiveString);
    }

    //
//
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Добавление нового задания (по кнопке submit)
        // Формирование нового задания
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String categoryTMP = req.getParameter("category");
        String deadline = req.getParameter("deadline");
        String priorityTMP = req.getParameter("priority");
        System.out.println("name = " + name);
        System.out.println("description = " + description);
        System.out.println("categoryTMP = " + categoryTMP);
        System.out.println("deadline = " + deadline);
        System.out.println("priorityTMP = " + priorityTMP);

        if (name != null && !name.isBlank() &&
            description != null && !description.isBlank() &&
            categoryTMP != null && !categoryTMP.isBlank() &&
            deadline != null && !deadline.isBlank() &&
            priorityTMP != null && !priorityTMP.isBlank()) {

            Category category = null;
            Category[] categories = Category.values();
            for (Category cat : categories) {
                if (cat.category().equals(categoryTMP)) {
                    category = cat;
                }
            }
//            System.out.println("category = " + category.toString());

            Priority priority = null;
            Priority[] priorities = Priority.values();
            for (Priority prior : priorities) {
                if (prior.priority().equals(priorityTMP)) {
                    priority = prior;
                }
            }
//            System.out.println("priority = " + priority.toString());

            // Считывание с файла
            readFromFile(req, path);

            // Добавление нового task в tasks
            try {
                tasks.add(new Task(name, description, category, deadline, priority));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Запись в файл
            writeInFile(req, path);
        }


        // 2. Сортировка (по выбору метода сортировки, т.е. без кнопки submit)
        String sort = req.getParameter("sort");
        System.out.println("sort = " + sort);
        if (sort != null && !sort.isBlank()) {
            // Считывание с файла
            readFromFile(req, path);

            // Сортировка
            switch (sort) {
                case "Category" ->
                        tasks = tasks.stream().sorted(Comparator.comparingInt(e -> e.getCategory().num())).collect(Collectors.toList());
                case "Priority" ->
                        tasks = tasks.stream().sorted((e1, e2) -> e1.getPriority().num() - e2.getPriority().num()).collect(Collectors.toList());
                case "Deadline" ->
                        tasks = tasks.stream().sorted((e1, e2) -> e1.getDeadline().compareTo(e2.getDeadline())).collect(Collectors.toList());
                default -> System.out.println("Default of sort");
            }

            // Запись в файл
            writeInFile(req, path);

        }

//        doGet(req, resp);
        resp.sendRedirect("/Lesson070/");
    }


}
