import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        // Часть 1: Конвертирование CSV в JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String inputCsvFile = "data.csv";
        String outputJsonFile = "data.json";

        // Чтение CSV и получение списка сотрудников
        List<Employee> employeesFromCsv = parseCSV(columnMapping, inputCsvFile);

        // Преобразование списка в JSON строку
        String jsonFromCsv = listToJson(employeesFromCsv);

        // Запись JSON строки в файл
        writeString(jsonFromCsv, outputJsonFile);

        System.out.println("JSON успешно записан в файл: " + outputJsonFile);

        // Часть 2: Конвертирование XML в JSON
        String inputXmlFile = "data.xml";
        String outputJsonFile2 = "data2.json";

        // Чтение XML и получение списка сотрудников
        List<Employee> employeesFromXml = parseXML(inputXmlFile);

        // Преобразование списка в JSON строку
        String jsonFromXml = listToJson(employeesFromXml);

        // Запись JSON строки в файл
        writeString(jsonFromXml, outputJsonFile2);

        System.out.println("JSON успешно записан в файл: " + outputJsonFile2);
    }

    // Метод для чтения CSV файла и получения списка сотрудников
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) throws IOException {
        // Чтение CSV файла
        FileReader reader = new FileReader(fileName);

        // Определение стратегии отображения колонок
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);

        // Парсинг CSV в объекты Employee
        CsvToBean<Employee> csvToBean = new CsvToBeanBuilder(reader)
                .withMappingStrategy(strategy)
                .withSkipLines(0)
                .build();

        // Получаем список сотрудников
        List<Employee> employees = csvToBean.parse();

        // Закрываем поток
        reader.close();

        return employees;
    }

    // Метод для чтения XML файла и получения списка сотрудников
    private static List<Employee> parseXML(String xmlFileName) throws Exception {
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
    private static String listToJson(List<Employee> employees) {
        // Создаем Gson с параметром pretty printing
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  // Включаем форматирование JSON
                .create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(employees, listType);
    }

    // Метод для записи строки в файл
    private static void writeString(String content, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(content);
        writer.close();
    }
}