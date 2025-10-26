# Favourite Contacts Feature - Implementation Summary

## Branch: `feature-favourite-contacts`

## Overview

Successfully implemented a favourite contacts feature that allows users to mark/unmark contacts as favourites with visual indicators. The implementation supports both index-based and name-based selection.

## Changes Made

### 1. Model Layer

- **Person.java**: Added `isFavourite` boolean field with getter method, updated constructor, equals(), hashCode(), and toString()
- **JsonAdaptedPerson.java**: Added serialization/deserialization support for `isFavourite` with backward compatibility (defaults to false for existing data)

### 2. Command Layer

- **FavouriteCommand.java**: New command to mark contacts as favourite
  - Supports both index (`favourite 1`) and name (`favourite John Doe`) syntax
  - Handles duplicate favouriting with appropriate error messages
  - Handles multiple name matches by prompting user to use index
- **UnfavouriteCommand.java**: New command to remove favourite status

  - Mirrors FavouriteCommand functionality
  - Handles appropriate error messages for non-favourite contacts

- **FavouriteCommandParser.java**: Parser for favourite command
  - Tries to parse as index first, falls back to name parsing
- **UnfavouriteCommandParser.java**: Parser for unfavourite command
  - Same parsing strategy as FavouriteCommandParser

### 3. Parser Registration

- **CourseBookParser.java**: Registered both `favourite` and `unfavourite` commands in the main parser switch statement

### 4. Updated Existing Commands

All commands that create Person objects were updated to handle the new `isFavourite` parameter:

- **AddCommand/AddCommandParser**: New persons default to `isFavourite=false`
- **EditCommand**: Preserves `isFavourite` status when editing other fields
- **AddCourseCommand**: Preserves `isFavourite` when adding courses
- **RemoveCourseCommand**: Preserves `isFavourite` when removing courses
- **BirthdayCommand**: Preserves `isFavourite` when updating birthday
- **ModelManager**: Preserves `isFavourite` when updating course colors

### 5. UI Layer

- **PersonCard.java**: Added favourite icon display logic
  - Shows gold star (★) for favourited contacts
  - Hides icon for non-favourite contacts
- **PersonListCard.fxml**: Added `favouriteIcon` Label element positioned before the person's index

- **DarkTheme.css**: Added `.favourite-star` style class
  - Gold color (#FFD700)
  - 14pt font size
  - Bold weight
  - Appropriate padding

### 6. Test Utilities

- **PersonBuilder.java**: Added `isFavourite` field with `withFavourite()` builder method
- **SampleDataUtil.java**: All sample persons default to `isFavourite=false`
- **JsonAdaptedPersonTest.java**: Updated all test constructors to include `isFavourite` parameter
- **BirthdayCommandTest.java**: Added `setCourseColor()` stub method to ModelStubWithOnePerson

## Usage Examples

### Mark as Favourite

```
favourite 1                 // By index
favourite John Doe          // By name
```

### Remove from Favourites

```
unfavourite 1               // By index
unfavourite John Doe        // By name
```

### Visual Indicator

Favourited contacts will display with a gold star (★) before their index number:

```
★ 1. John Doe
```

## Future Integration Notes

- The `isFavourite` field is ready for filtering via predicates
- A future `listfav` command can use: `model.updateFilteredPersonList(Person::isFavourite)`
- Could be extended to allow editing favourite status through the `edit` command by adding it to `EditPersonDescriptor`

## Data Persistence

- The favourite status is persisted in the JSON data file
- Backward compatible: existing data files without the `isFavourite` field will load successfully (defaulting to false)
- Forward compatible: data files with `isFavourite` field are properly stored and loaded

## Build Status

✅ All compilation successful
✅ All checkstyle rules passed
✅ Ready for testing
