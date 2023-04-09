package org.itstep;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DoneServlet extends HttpServlet {

    private static File file = new File("e:\\JAVA\\Projects\\GitHub Homeworks\\Lesson070\\src\\main\\webapp\\resources\\db\\tasks.db");

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

    private void writeInFile() {
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

    private void readFromFile() {
        tasks = null;
        System.out.println(file.exists());
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file);
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
            readFromFile();
            // Удаление со списка
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(delete)) {
                    tasks.remove(tasks.get(i));
                }
            }
            // Запись в файл
            writeInFile();
        }


        // 3. Изменение condition (состояние)
        String condition = req.getParameter("condition");
        System.out.println("conditionDone = " + condition);
        if (condition != null && !condition.isBlank()) {
            // Считывание с файла
            readFromFile();
            // Изменение состояния на Active
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == Integer.parseInt(condition)) {
                    tasks.get(i).setCondition(Condition.ACTIVE);
                }
            }
            // Запись в файл
            writeInFile();
        }


        // 4. Вывод HTML
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        // Считывание с файла
        readFromFile();
//        System.out.println("DoneServlet");
//        if (tasks != null) {
//            tasks.stream().forEach(task -> {
//                System.out.println(task.toString());
//            });
//        }
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



