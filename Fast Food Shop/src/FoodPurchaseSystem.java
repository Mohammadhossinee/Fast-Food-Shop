import java.io.*;
import java.util.*;


public class FoodPurchaseSystem {

    private static final String USER_DATA_FILE = "user_data.txt";
    private static final String MENU_DATA_FILE = "Menu.txt";
    private static final String DISCOUNT_DATA_FILE = "Discount.txt";
    private static Scanner scanner = new Scanner(System.in);

    private static Map<Character, String> huffmanCodes = new HashMap<>();
    private static Set<String> validDiscountCodes = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Food Purchase System!");

        buildHuffmanTree();
        decodeDiscountCodes(loadDiscountCodesFromFile());

        while (true) {
            System.out.println("====================================");
            System.out.println("|                                  |");
            System.out.println("|     WELCOME TO OUR SYSTEM        |");
            System.out.println("|                                  |");
            System.out.println("====================================");
            System.out.println("|                                  |");
            System.out.println("|  1. Register                     |");
            System.out.println("|  2. Login                        |");
            System.out.println("|  3. Exit                         |");
            System.out.println("|                                  |");
            System.out.println("====================================");
            System.out.print("Please enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    if (loginUser()) {
                        showMenu();
                    }
                    break;
                case 3:
                    System.out.println("Exiting the Food Purchase System. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    public static void registerUser() {
        try (FileWriter writer = new FileWriter(USER_DATA_FILE, true)) {
            System.out.print("Enter your username: ");
            String username = scanner.next();

            System.out.print("Enter your password: ");
            String password = scanner.next();

            int hash = calculateHash(password);

            writer.write(username + ":" + hash + "\n");

            System.out.println("Registration successful. Please login to continue.");
            System.out.println();

        } catch (IOException e) {
            System.out.println("An error occurred during registration.");
            e.printStackTrace();
        }
    }

    public static boolean loginUser() {
        try (Scanner fileScanner = new Scanner(new File(USER_DATA_FILE))) {
            System.out.print("Enter your username: ");
            String username = scanner.next();

            System.out.print("Enter your password: ");
            String password = scanner.next();

            int hash = calculateHash(password);
            boolean found = false;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(":");
                if (parts[0].equals(username) && Integer.parseInt(parts[1]) == hash) {
                    System.out.println("Login successful. Welcome, " + username + "!");
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Invalid username or password.");
            }

            System.out.println(); 
            return found;

        } catch (IOException e) {
            System.out.println("An error occurred while reading user data.");
            e.printStackTrace();
            return false;
        }
    }

    public static int calculateHash(String password) {
        int hash = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            int asciiValue = (int) c;
            hash += asciiValue * (i + 1) - (i + 1);
        }
        return hash;
    }

    public static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); 
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    public static void showMenu() {
        List<FoodItem> menu = loadMenu();
        if (menu.isEmpty()) {
            System.out.println("The menu is currently empty.");
            return;
        }

        boolean ordering = true;
        while (ordering) {
            System.out.println("Food Menu:");
            for (int i = 0; i < menu.size(); i++) {
                System.out.println((i + 1) + ". " + menu.get(i));
            }

            System.out.println("=======================================");
            System.out.println("|                                     |");
            System.out.println("|  1. Sort by Price                   |");
            System.out.println("|  2. Order Food                      |");
            System.out.println("|  3. Exit                            |");
            System.out.println("|                                     |");
            System.out.println("=======================================");
            System.out.print("Please enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    sortMenuByPrice(menu);
                    break;
                case 2:
                    orderFood(menu);
                    break;
                case 3:
                    ordering = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    public static List<FoodItem> loadMenu() {
        List<FoodItem> menu = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(MENU_DATA_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" - ");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].substring(1).trim());
                    int prepTime = Integer.parseInt(parts[2].substring(0, parts[2].length() - 1).trim());
                    menu.add(new FoodItem(name, price, prepTime));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Menu file not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid format in menu file.");
        }
        return menu;
    }

    public static void sortMenuByPrice(List<FoodItem> menu) {
        heapSort(menu);
        System.out.println("Menu sorted by price.");
    }

    public static void heapSort(List<FoodItem> menu) {
        int n = menu.size();

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(menu, n, i);
        }

        for (int i = n - 1; i >= 0; i--) {
            Collections.swap(menu, 0, i);
            heapify(menu, i, 0);
        }
    }

    public static void heapify(List<FoodItem> menu, int n, int i) {
        int largest = i; 
        int left = 2 * i + 1; 
        int right = 2 * i + 2; 


        if (left < n && menu.get(left).price > menu.get(largest).price) {
            largest = left;
        }

        if (right < n && menu.get(right).price > menu.get(largest).price) {
            largest = right;
        }

        if (largest != i) {
            Collections.swap(menu, i, largest);
            heapify(menu, n, largest);
        }
    }

    public static void orderFood(List<FoodItem> menu) {
        System.out.println("Enter the numbers of the items you want to order, separated by spaces:");
        String[] orderInput = scanner.nextLine().split(" ");
        double total = 0;
        List<FoodItem> orderedItems = new ArrayList<>();
        for (String input : orderInput) {
            try {
                int itemIndex = Integer.parseInt(input) - 1;
                if (itemIndex >= 0 && itemIndex < menu.size()) {
                    FoodItem item = menu.get(itemIndex);
                    orderedItems.add(item);
                    total += item.price;
                } else {
                    System.out.println("Invalid item number: " + input);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + input);
            }
        }

        if (!orderedItems.isEmpty()) {
            System.out.println("You have ordered:");
            for (FoodItem item : orderedItems) {
                System.out.println(item);
            }
            System.out.println("Total price: $" + total);

            System.out.println("Enter discount code if you have any (or press Enter to skip):");
            String discountCode = scanner.nextLine();
            if (!discountCode.isEmpty() && validDiscountCodes.contains(discountCode)) {
                total *= 0.90; 
                System.out.println("Valid discount code applied. New total price: $" + total);
            } else if (!discountCode.isEmpty()) {
                System.out.println("Invalid discount code.");
            }

            System.out.println("Enter the destination (A, B, C, D, E, F, G, H):");
            char destination = scanner.nextLine().charAt(0);
            showRoute('A', destination);
        } else {
            System.out.println("No valid items ordered.");
        }
    }

    public static void buildHuffmanTree() {
        // Frequency table
        Map<Character, Integer> frequencyMap = new HashMap<>();
        frequencyMap.put('A', 5);
        frequencyMap.put('B', 6);
        frequencyMap.put('C', 7);
        frequencyMap.put('D', 8);
        frequencyMap.put('E', 10);
        frequencyMap.put('F', 16);

        // Priority queue to build the Huffman tree
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode newNode = new HuffmanNode('\0', left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            pq.add(newNode);
        }

        HuffmanNode root = pq.poll();
        generateHuffmanCodes(root, "");
    }

    public static void generateHuffmanCodes(HuffmanNode node, String code) {
        if (node == null) return;
        if (node.c != '\0') {
            huffmanCodes.put(node.c, code);
        }
        generateHuffmanCodes(node.left, code + "0");
        generateHuffmanCodes(node.right, code + "1");
    }

    public static List<String> loadDiscountCodesFromFile() {
        List<String> binaryCodes = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(DISCOUNT_DATA_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                binaryCodes.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Discount codes file not found.");
        }
        return binaryCodes;
    }

    public static void decodeDiscountCodes(List<String> binaryCodes) {
        HuffmanNode root = buildHuffmanTreeForDecoding();
        for (String binaryCode : binaryCodes) {
            HuffmanNode currentNode = root;
            StringBuilder decodedCode = new StringBuilder();
            for (char bit : binaryCode.toCharArray()) {
                if (bit == '0') {
                    currentNode = currentNode.left;
                } else {
                    currentNode = currentNode.right;
                }

                if (currentNode.left == null && currentNode.right == null) {
                    decodedCode.append(currentNode.c);
                    currentNode = root;
                }
            }
            validDiscountCodes.add(decodedCode.toString());
        }
    }

    public static HuffmanNode buildHuffmanTreeForDecoding() {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        frequencyMap.put('A', 5);
        frequencyMap.put('B', 6);
        frequencyMap.put('C', 7);
        frequencyMap.put('D', 8);
        frequencyMap.put('E', 10);
        frequencyMap.put('F', 16);

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode newNode = new HuffmanNode('\0', left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            pq.add(newNode);
        }
        return pq.poll();
    }

    public static void showRoute(char start, char end) {
        Map<Character, List<Character>> graph = buildGraph();
        List<Character> route = findShortestPath(graph, start, end);
        if (route == null) {
            System.out.println("No route found from " + start + " to " + end + ".");
        } else {
            System.out.println("Route from " + start + " to " + end + ": " + route);
        }
    }

    public static Map<Character, List<Character>> buildGraph() {
        Map<Character, List<Character>> graph = new HashMap<>();
        graph.put('A', Arrays.asList('G', 'D'));
        graph.put('B', Arrays.asList('G', 'C', 'E'));
        graph.put('C', Arrays.asList('B', 'H'));
        graph.put('D', Arrays.asList('H', 'A', 'E'));
        graph.put('E', Arrays.asList('D', 'B'));
        graph.put('F', Arrays.asList('H'));
        graph.put('G', Arrays.asList('A', 'B'));
        graph.put('H', Arrays.asList('F', 'C', 'D', 'B'));
        return graph;
    }

    public static List<Character> findShortestPath(Map<Character, List<Character>> graph, char start, char end) {
        Map<Character, Character> predecessors = new HashMap<>();
        Map<Character, Integer> distances = new HashMap<>();
        for (Character node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        PriorityQueue<Character> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            char current = queue.poll();
            for (char neighbor : graph.get(current)) {
                int newDist = distances.get(current) + 1;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<Character> path = new ArrayList<>();
        for (Character at = end; at != null; at = predecessors.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        if (path.get(0) != start) {
            return null;
        }
        return path;
    }
}

