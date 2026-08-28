# Alfred UI Test Plan

## Test Session Information

- Project root: repository root
- Runtime: Java `25.0.3.fx-zulu`
- Compile command:

  ```bash
  rm -rf .alfred-classes
  mkdir .alfred-classes
  javac -d .alfred-classes $(find src/main/java -name '*.java')
  ```

- Run command:

  ```bash
  java -cp .alfred-classes alfred.Alfred
  ```

- Comparison rule: compare captured output with the expected output. Ignore only the banner and divider formatting if the test runner explicitly documents that normalization. Task messages, ordering, status icons, and date/time strings must match exactly.
- Isolation: delete `data/alfred.txt` (or the whole `data/` folder) before each case so leftover saved tasks do not affect later cases.
- Failure policy: stop immediately after the first failed test case and report the complete actual and expected outputs.

## Test Cases

### 1. Add and List ToDo

**Aim:** Verify that a todo is stored and displayed with the `[T]` type icon and incomplete status.

**Inputs:**

```text
todo visit new theme park
list
bye
```

**Expected output:**

```text
Very good. I've added this task:
  [T][ ] visit new theme park
You now have 1 tasks in your list.
Certainly. Here are the tasks in your list:
1.[T][ ] visit new theme park
Until next time. I shall be here should you require me.
```

### 2. Add and List Deadline

**Aim:** Verify that a deadline stores a parsed `/by` date and displays it as `MMM dd yyyy`.

**Inputs:**

```text
deadline submit report /by 2019-10-15
list
bye
```

**Expected output:**

```text
Very good. I've added this task:
  [D][ ] submit report (by: Oct 15 2019)
You now have 1 tasks in your list.
Certainly. Here are the tasks in your list:
1.[D][ ] submit report (by: Oct 15 2019)
Until next time. I shall be here should you require me.
```

### 3. Add and List Event

**Aim:** Verify that an event stores parsed `/from` and `/to` dates and displays them as `MMM dd yyyy`.

**Inputs:**

```text
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

**Expected output:**

```text
Very good. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
You now have 1 tasks in your list.
Certainly. Here are the tasks in your list:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Until next time. I shall be here should you require me.
```

### 4. Mark and Unmark Typed Tasks

**Aim:** Verify that inherited completion behavior works for todo, deadline, and event tasks.

**Inputs:**

```text
todo read book
deadline return book /by 2019-10-15
event team meeting /from 2019-10-15 /to 2019-10-15
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
Very good. I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
Certainly. Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
3.[E][ ] team meeting (from: Oct 15 2019 to: Oct 15 2019)
Certainly. I've marked this task as not done:
  [D][ ] return book (by: Oct 15 2019)
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] team meeting (from: Oct 15 2019 to: Oct 15 2019)
Until next time. I shall be here should you require me.
```

## Error and State-Preservation Cases

These cases alternate invalid and valid inputs. Each invalid input is followed by a list or a valid input so the test checks that rejecting it does not change Alfred's internal task state.

### 5. Empty Todo Description

**Aim:** Reject `todo` without a description and preserve the task list while accepting a subsequent valid todo.

**Inputs:**

```text
todo
todo buy milk
list
bye
```

**Expected output:**

```text
I'm afraid I must report: I'm afraid a todo requires a description, sir.
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[T][ ] buy milk
Bye. Hope to see you again!
```

### 6. Valid Deadline After Empty Todo

**Aim:** Verify that a valid deadline can still be added after a rejected empty todo.

**Inputs:**

```text
todo
deadline submit report /by 2019-10-15
list
bye
```

**Expected output:**

```text
I'm afraid I must report: a todo requires a description, sir.
Very good. I've added this task:
  [D][ ] submit report (by: Oct 15 2019)
You now have 1 tasks in your list.
Certainly. Here are the tasks in your list:
1.[D][ ] submit report (by: Oct 15 2019)
Until next time. I shall be here should you require me.
```

### 7. Unknown Command

**Aim:** Reject an unrecognized command and ensure it is not stored as a task.

**Inputs:**

```text
todo keep this task
blah
list
bye
```

**Expected output:**

```text
Got it. I've added this task:
  [T][ ] keep this task
Now you have 1 tasks in the list.
I'm afraid I must report: I'm afraid I do not recognise that request, sir.
Here are the tasks in your list:
1.[T][ ] keep this task
Bye. Hope to see you again soon!
```

### 8. Valid Event After Unknown Command

**Aim:** Verify that an event is stored correctly after an unknown command is rejected and does not affect task numbering.

**Inputs:**

```text
blah
event project meeting /from 2019-10-15 /to 2019-10-15
list
bye
```

**Expected output:**

```text
I'm afraid I must report: I do not recognise that request, sir.
Very good. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 15 2019)
You now have 1 tasks in your list.
Certainly. Here are the tasks in your list:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 15 2019)
Until next time. I shall be here should you require me.
```

### 9. Malformed Deadline

**Aim:** Reject a deadline without a `/by` clause and preserve a previously stored task.

**Inputs:**

```text
todo read book
deadline submit report
list
bye
```

**Expected output:**

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
I'm afraid I must report: a deadline needs a description and a /by date or time, sir.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

### 10. Malformed Event

**Aim:** Reject an event without a complete `/from` and `/to` pair and preserve a previously stored task.

**Inputs:**

```text
todo attend class
event project meeting /from Mon 2pm
list
bye
```

**Expected output:**

```text
Got it. I've added this task:
  [T][ ] attend class
Now you have 1 tasks in the list.
I'm afraid I must report: an event needs a description, a /from time, and a /to time, sir.
Here are the tasks in your list:
1.[T][ ] attend class
Bye. Hope to see you again soon!
```

### 11. Invalid Mark and Unmark Indices

**Aim:** Reject invalid task indices without changing the stored task or its completion status.

**Inputs:**

```text
todo read book
mark 2
unmark abc
list
bye
```

**Expected output:**

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
I'm afraid I must report: that task number does not exist, sir.
I'm afraid I must report: please provide a valid task number, sir.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

### 12. Empty Deadline and Event Fields

**Aim:** Reject commands with empty descriptions or empty date/time fields, and verify that neither invalid command changes the task count.

**Inputs:**

```text
deadline /by Sunday
event meeting /from Mon 2pm /to
list
bye
```

**Expected output:**

```text
I'm afraid I must report: a deadline needs a description, sir.
I'm afraid I must report: an event needs a description and both date/time fields, sir.
Certainly. Here are the tasks in your list:
Until next time. I shall be here should you require me.
```

### 13. Unrecognized Date

**Aim:** Reject a free-text deadline date and leave the task list empty.

**Inputs:**

```text
deadline return book /by Sunday
list
bye
```

**Expected output:**

```text
I'm afraid I must report: I need a date as yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm, sir.
Certainly. Here are the tasks in your list:
Until next time. I shall be here should you require me.
```

### 14. List Tasks On a Date

**Aim:** List only deadlines and events that occur on the given date, keeping original indices.

**Inputs:**

```text
todo read book
deadline return book /by 2019-10-15
deadline later /by 2019-10-16
event camp /from 2019-10-14 /to 2019-10-16
on 2019-10-15
bye
```

**Expected output:**

```text
Certainly. Here are the deadlines and events on Oct 15 2019:
2.[D][ ] return book (by: Oct 15 2019)
4.[E][ ] camp (from: Oct 14 2019 to: Oct 16 2019)
Until next time. I shall be here should you require me.
```
