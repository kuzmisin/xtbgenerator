# XtbGenerator

Generate/append XTB (translation XML file) for google closure compiler https://developers.google.com/closure/compiler/

## XTB file
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE translationbundle>
<translationbundle lang="cs">
    <translation id="1234567890">Message ...</translation>
    <translation id="1234567891">Message with <ph name="PLACE_HOLDER" />.</translation>
    ...
</translationbundle>
```

## JS file
```javascript
/** @desc Description for Test 1 */
var MSG_TEST_1 = goog.getMsg('Test 1');

/** @desc Message with placeholder */
var MSG_TEST_2 = goog.getMsg('Message with {$placeHolder}.', {placeHolder: 'replaced text'});
```

## Usage
```
Usage: XtbGenerator --lang <arg> [--projectId <arg>] --js <FILE1> [--js <FILE2>]
or: XtbGenerator --lang <arg> [--projectId <arg>] FILE1 [FILE2]

Params:
    --lang          : Lang
    --projectId     : Project ID
    --js            : Input JS file
    --translations_file : XTB translation file
    --xtb_output_file   : XTB output file
```

## Stand alone usage
Create message file
```
java -jar XtbGenerator.jar \
	--lang cs \
	--xtb_output_file messages.xtb \
	--js messages.js
```

Append to existing message file
```
java -jar XtbGenerator.jar \
	--lang cs \
	--translations_file messages.xtb \
	--xtb_output_file messages.xtb \
	--js messages.js
```

## Usage with closurebuilder.py
```
closurebuilder.py \
	--root js/ \
	--root js-closure/ \
	--input js/app.js \
	--output_mode=compiled \
	--compiler_jar=XtbGenerator.jar \
	--jvm_flags="-d64" \
	--compiler_flags="--translations_file=messages.xtb" \
	--compiler_flags="--xtb_output_file=messages.xtb" \
	--compiler_flags="--lang=cs"
```

You can find more about using ```closurebuilder.py``` on http://www.closurecheatsheet.com/skeleton

## Google closure

More about google closure:
- [ClosureCheatSheet](http://closurecheatsheet.com/)
- [Google closure compiler](https://developers.google.com/closure/compiler/)

## Download
https://raw.github.com/kuzmisin/xtbgenerator/master/bin/XtbGenerator.jar

## TODO
- add meaning into XTB file (depend on visibility of JsMessage.java::getMeaning)
