import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        // Читаем XML файл и получаем список сотрудников
        List<Employee> employees = parseXML("data.xml");

        // Преобразуем список в JSON строку
        String json = listToJson(employees);

        // Записываем JSON в файл
        writeString(json, "data2.json");

        System.out.println("Данные успешно сохранены в файл data2.json");
    }

    // Метод для чтения XML файла и создания списка сотрудников
    public static List<Employee> parseXML(String xmlFileName) throws Exception {
        // Создание фабрики для построения документа
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Загрузка XML-документа
        Document document = builder.parse(new File(xmlFileName));

        // Получение корневого элемента
        Element rootElement = document.getDocumentElement();

        // Получение списка узлов с сотрудниками
        NodeList nodeList = rootElement.getElementsByTagName("employee");

        // Список для хранения объектов Employee
        List<Employee> employees = new ArrayList<>();

        // Проход по каждому узлу сотрудника
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Извлекаем значения из тегов
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                // Создаем объект Employee и добавляем его в список
                Employee employee = new Employee(id, firstName, lastName, country, age);
                employees.add(employee);
            }
        }

        return employees;
    }

    // Метод для преобразования списка объектов в JSON строку
    public static String listToJson(List<Employee> employees) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  // Этот параметр включает форматирование JSON
                .create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(employees, listType);
    }

    // Метод для записи строки в файл
    public static void writeString(String content, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(content);
        writer.close();
    }
}