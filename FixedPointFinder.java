public class FixedPointFinder {
    // Function to find the fixed point in a sorted array
    // Returns the fixed point if one exists, otherwise returns -1
    static int findFixedPoint(int[] arr) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == mid) {
                return mid; // Found the fixed point
            } else if (arr[mid] < mid) {
                left = mid + 1; // Search the right half
            } else {
                right = mid - 1; // Search the left half
            }
        }

        return -1; // No fixed point found
    }

    // Main method to test the function
    public static void main(String[] args) {
        int[] arr1 = {-6, 0, 1, 3};
        int[] arr2 = {1, 5, 7, 8};

        int fixedPoint1 = findFixedPoint(arr1);
        if (fixedPoint1 != -1) {
            System.out.println("Fixed Point in arr1: " + arr1[fixedPoint1]);
        } else {
            System.out.println("No fixed point in arr1");
        }

        int fixedPoint2 = findFixedPoint(arr2);
        if (fixedPoint2 != -1) {
            System.out.println("Fixed Point in arr2: " + arr2[fixedPoint2]);
        } else {
            System.out.println("No fixed point in arr2");
        }
    }
}
