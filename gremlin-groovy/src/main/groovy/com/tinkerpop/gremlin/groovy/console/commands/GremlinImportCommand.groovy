package com.tinkerpop.gremlin.groovy.console.commands

import jline.console.completer.AggregateCompleter
import jline.console.completer.ArgumentCompleter
import jline.console.completer.Completer
import jline.console.completer.StringsCompleter
import org.codehaus.groovy.control.CompilationFailedException

import org.codehaus.groovy.tools.shell.CommandSupport
import org.codehaus.groovy.tools.shell.Groovysh
import org.codehaus.groovy.tools.shell.Interpreter
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletor
import org.codehaus.groovy.tools.shell.util.Logger
import org.codehaus.groovy.tools.shell.util.PackageHelper


/**
 * Temporary import command until bug is fixed in groovy-core.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
class GremlinImportCommand extends CommandSupport {

    GremlinImportCommand(final Groovysh shell){
        super(shell, 'import', ':i')
    }

    @Override
    Completer getCompleter() {
        // need a different completer setup due to static import
        Completer impCompleter = new StringsCompleter(name, shortcut)
        Completer asCompleter = new StringsCompleter('as')
        PackageHelper packageHelper = shell.packageHelper
        Interpreter interp = shell.interp
        Collection<Completer> argCompleters = [
                (Completer) new ArgumentCompleter([
                        impCompleter,
                        new ImportCompleter(packageHelper, interp, false),
                        asCompleter,
                        null]),
                (Completer) new ArgumentCompleter([
                        impCompleter,
                        new StringsCompleter('static'),
                        new ImportCompleter(packageHelper, interp, true),
                        asCompleter,
                        null])]
        return new AggregateCompleter(argCompleters)

    }

    Object execute(final List<String> args) {
        assert args != null

        if (args.isEmpty()) {
            fail("Command 'import' requires one or more arguments") // TODO: i18n
        }

        def importSpec = args.join(' ')
        if (! (importSpec ==~ '[\\da-zA-Z_. *]+;?$')) {
            def msg = "Invalid import definition: '${importSpec}'" // TODO: i18n
            log.debug(msg)
            fail(msg)
        }
        // remove last semicolon
        importSpec = importSpec.replaceAll(';', '')

        def buff = [ 'import ' + args.join(' ') ]
        buff << 'def dummp = false'

        def type
        try {
            type = classLoader.parseClass(buff.join(NEWLINE))

            // No need to keep duplicates, but order may be important so remove the previous def, since
            // the last defined import will win anyways

            if (imports.remove(importSpec)) {
                log.debug("Removed duplicate import from list")
            }

            log.debug("Adding import: $importSpec")

            imports.add(importSpec)
            return imports.join(', ')
        }
        catch (CompilationFailedException e) {
            def msg = "Invalid import definition: '${importSpec}'; reason: $e.message" // TODO: i18n
            log.debug(msg, e)
            fail(msg)
        }
        finally {
            // Remove the class generated while testing the import syntax
            classLoader.removeClassCacheEntry(type?.name)
        }
    }
}

class ImportCompleter implements Completer {

    PackageHelper packageHelper
    Groovysh shell
    protected final Logger log = Logger.create(ImportCompleter)
    /*
     * The following rules do not need to work for all thinkable situations,just for all reasonable situations.
     * In particular the underscore and dollar signs in Class or method names usually indicate something internal,
     * which we intentionally want to hide in tab completion
     */
    // matches fully qualified Classnames with dot at the end
    public final static String QUALIFIED_CLASS_DOT_PATTERN = /^[a-z_]{1}[a-z0-9_]*(\.[a-z0-9_]*)*\.[A-Z][^.]*\.$/
    // matches empty, packagenames or fully qualified classNames
    public final static String PACK_OR_CLASSNAME_PATTERN = /^([a-z_]{1}[a-z0-9_]*(\.[a-z0-9_]*)*(\.[A-Z][^.]*)?)?$/
    // matches empty, packagenames or fully qualified classNames without special symbols
    public final static String PACK_OR_SIMPLE_CLASSNAME_PATTERN = '^([a-z_]{1}[a-z0-9_]*(\\.[a-z0-9_]*)*(\\.[A-Z][^.\$_]*)?)?\$'
    // matches empty, packagenames or fully qualified classNames or fully qualified method names
    public final static String PACK_OR_CLASS_OR_METHODNAME_PATTERN = '^([a-z_]{1}[a-z0-9.]*(\\.[a-z0-9_]*)*(\\.[A-Z][^.\$_]*(\\.[a-zA-Z0-9_]*)?)?)?\$'


    boolean staticImport
    def interpreter


    public ImportCompleter(PackageHelper packageHelper, interp, boolean staticImport) {
        this.packageHelper = packageHelper
        this.staticImport = staticImport
        this.interpreter = interp
        this.shell = shell
    }

    @Override
    int complete(String buffer, int cursor, List result) {
        String currentImportExpression = buffer ? buffer.substring(0, cursor) : ""
        if (staticImport) {
            if (! (currentImportExpression ==~ PACK_OR_CLASS_OR_METHODNAME_PATTERN)) {
                return -1
            }
        } else {
            if (! (currentImportExpression ==~ PACK_OR_SIMPLE_CLASSNAME_PATTERN)) {
                return -1
            }
        }
        if (currentImportExpression.contains("..")) {
            return -1
        }

        if (currentImportExpression.endsWith('.')) {
            // no upper case?
            if (currentImportExpression ==~ /^[a-z0-9.]+$/) {
                Set<String> classnames = packageHelper.getContents(currentImportExpression[0..-2])
                if (classnames) {
                    if (staticImport) {
                        result.addAll(classnames.collect { String it -> it + "."})
                    } else {
                        result.addAll(classnames.collect { String it -> filterMatches(it) })
                    }
                }
                if (! staticImport) {
                    result.add('* ')
                }
                return currentImportExpression.length()
            } else if (staticImport && currentImportExpression ==~ QUALIFIED_CLASS_DOT_PATTERN) {
                Class clazz = interpreter.evaluate([currentImportExpression[0..-2]]) as Class
                if (clazz != null) {
                    Collection<String> members = ReflectionCompletor.getPublicFieldsAndMethods(clazz, "")
                    result.addAll(members.collect({ String it -> it.replace('(', '').replace(')', '') + " " }))
                }
                result.add('* ')
                return currentImportExpression.length()
            }
            return -1
        } // endif startswith '.', we have a prefix

        String prefix
        int lastDot = currentImportExpression.lastIndexOf('.')
        if (lastDot == -1) {
            prefix = currentImportExpression
        } else {
            prefix = currentImportExpression.substring(lastDot + 1)
        }
        String baseString = currentImportExpression.substring(0, Math.max(lastDot, 0))

        // expression could be for Classname, or for static methodname
        if (currentImportExpression ==~ PACK_OR_CLASSNAME_PATTERN) {
            Set<String> candidates = packageHelper.getContents(baseString)
            if (candidates == null || candidates.size() == 0) {
                // At least give standard package completion, else static keyword is highly annoying
                Collection<String> standards = org.codehaus.groovy.control.ResolveVisitor.DEFAULT_IMPORTS.findAll {String it -> it.startsWith(currentImportExpression)}
                if (standards) {
                    result.addAll(standards)
                    return 0
                }
                return -1
            }

            log.debug(prefix)
            Collection<String> matches = candidates.findAll { String it -> it.startsWith(prefix) }
            if (matches) {
                result.addAll(matches.collect { String it -> filterMatches(it) })
                return lastDot <= 0 ? 0 : lastDot + 1
            }
        } else if (staticImport) {
            Class clazz = interpreter.evaluate([baseString]) as Class
            if (clazz != null) {
                Collection<String> members = ReflectionCompletor.getPublicFieldsAndMethods(clazz, prefix)
                if (members) {
                    result.addAll(members.collect({ String it -> it.replace('(', '').replace(')', '') + " " }))
                    return lastDot <= 0 ? 0 : lastDot + 1
                }
            }
        }
        return -1
    }

    def filterMatches(String it) {
        if (it[0] in 'A' .. 'Z') {
            return it + ' '
        }
        return it + '.'
    }
}