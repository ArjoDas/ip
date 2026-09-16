# Alfred User Guide

Alfred is a chatbot that stores todos, deadlines, and events. Type one command per line in the GUI text field or in the console. Command words are case-sensitive and must be the first word on the line.

Dates use `yyyy-MM-dd` (for example `2019-10-15`) or `d/M/yyyy` (for example `2/12/2019`). You may add a 24-hour time after the date as `HHmm` (for example `1800`) or with a colon (for example `18:00`). Free-text dates such as `Sunday` are rejected.

Live task numbers are 1-based among tasks that are not archived. Use those numbers with `mark`, `unmark`, `delete`, and `archive`. Archive numbers from `list archive` are used with `restore` and `delete archive`. Adding, marking, unmarking, deleting, archiving, and restoring a task is saved to `data/alfred.txt`.

## Adding tasks

### `todo DESCRIPTION`

Adds a todo. The description cannot be blank.

```
todo read book
```

Alfred stores it as `[T][ ] read book` and reports how many live tasks are in the list.

### `deadline DESCRIPTION /by DATE`

Adds a deadline. Both a description and a `/by` date (or date-time) are required.

```
deadline submit report /by 2019-10-15
deadline return book /by 2/12/2019 1800
```

Dates display as `MMM dd yyyy` (for example `Oct 15 2019`). Times display as `MMM dd yyyy, h:mma` (for example `Dec 02 2019, 6:00PM`).

### `event DESCRIPTION /from START /to END`

Adds an event. `/from` must come before `/to`, and the end cannot be earlier than the start.

```
event project meeting /from 2019-10-15 /to 2019-10-16
```

Shown as `[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)`.

## Listing and finding tasks

### `list`

Prints every live task in insertion order, numbered from 1. Archived tasks are hidden.

### `list archive`

Prints archived tasks in file order, numbered from 1 among archived tasks. If none are archived, Alfred replies `None, sir.` after the archive header.

### `find KEYWORD`

Lists live tasks whose description contains `KEYWORD`, ignoring case. Date and time text is not searched. Archived tasks are skipped. Matches are numbered from 1 in the order they appear.

```
find book
```

If nothing matches, Alfred replies `None, sir.`

### `on DATE`

Lists live deadlines due on that calendar date, and live events whose start-to-end range includes it. Todos never match. Matching tasks keep their current `list` numbers.

```
on 2019-10-15
```

If nothing matches, Alfred replies `None, sir.`

## Updating tasks

### `mark INDEX` and `unmark INDEX`

Marks the live task at that list number as done (`[X]`) or not done (`[ ]`).

```
mark 2
unmark 2
```

### `delete INDEX`

Permanently removes the live task at that list number. Later live tasks are renumbered. This does not archive the task.

```
delete 2
```

## Archive and restore

Archive hides a live task from `list`, `find`, `on`, `mark`, `unmark`, and `delete` without discarding it. The task stays in the same save file, marked archived (`A`) instead of live (`UA`).

### `archive INDEX`

Archives the live task at that list number. Alfred reports how many tasks were archived, using `N tasks` even when the count is 1.

```
archive 2
```

### `archive all`

Archives every remaining live task. Already archived tasks are left unchanged. If the live list is empty, Alfred reports an error.

```
archive all
```

If that leaves no live tasks, Alfred also says `Your list is empty.`

### `restore INDEX`

Restores the archived task at that `list archive` number to the end of the live list.

```
restore 1
```

### `restore all`

Restores every archived task to the end of the live list, in current archive-list order. If nothing is archived, Alfred reports an error.

```
restore all
```

### `delete archive INDEX`

Permanently removes the archived task at that `list archive` number. There is no bulk archive delete.

```
delete archive 1
```

## Exiting

### `bye`

Ends the session. Alfred replies `Until next time. I shall be here should you require me.` `bye` must be typed exactly, with no extra text.

Unrecognized commands (for example `blah`), missing arguments, invalid dates, extra arguments, and out-of-range task numbers are reported as errors and do not change the task list.
