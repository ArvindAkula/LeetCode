class PrintImmutableLinkedListReverse {
    public void printLinkedListInReverse(ImmutableListNode head) {

        Stack<ImmutableListNode> stack = new Stack<>();
        while (head != null) {
            stack.push(head);
            head = head.getNext();
        }
        while (!stack.isEmpty()) {
            stack.pop().printValue();
        }
    }
}