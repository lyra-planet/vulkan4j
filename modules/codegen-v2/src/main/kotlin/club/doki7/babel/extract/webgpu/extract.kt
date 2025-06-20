package club.doki7.babel.extract.webgpu

import club.doki7.babel.extract.ensureLowerCamelCase
import club.doki7.babel.extract.toPascalCase
import club.doki7.babel.registry.*
import club.doki7.babel.util.Either
import club.doki7.babel.util.parseYML
import club.doki7.babel.util.query
import java.math.BigInteger
import java.util.logging.Logger
import kotlin.io.path.Path

private val inputDir = Path("codegen-v2/input")
internal val log = Logger.getLogger("c.d.b.extract.webgpu")

fun main() {
    val ymlString = inputDir.resolve("webgpu.yml").toFile().readText()
    val parsedYML = ymlString.parseYML()
    parsedYML.extractEntities()
}

fun extractWebGPURegistry(): Registry<EmptyMergeable> {
    val ymlString = inputDir.resolve("webgpu.yml").toFile().readText()
    val parsedYML = ymlString.parseYML()
    var r = parsedYML.extractEntities()
    r.renameEntities()
    return r
}

private fun Map<String, Any>.extractEntities(): Registry<EmptyMergeable> {
    val constants = extractConstants()
    val enumerations = extractEnumerations()
    val bitmasks = extractBitmasks()
    val opaqueHandleTypedefs = extractOpaqueHandleTypedefs()
    val functionTypedefs = extractFunctionTypedefs()
    val structures = extractStructures()
    return Registry(
            aliases = mutableMapOf(),
            bitmasks = bitmasks,
            constants = constants,
            commands = mutableMapOf(),
            enumerations = enumerations,
            functionTypedefs = functionTypedefs,
            opaqueHandleTypedefs = opaqueHandleTypedefs,
            opaqueTypedefs = mutableMapOf(),
            structures = structures,
            unions = mutableMapOf(),
            ext = EmptyMergeable()
    )
}
private fun Map<String, Any>.extractOpaqueHandleTypedefs(): MutableMap<Identifier, OpaqueHandleTypedef> {
    val opaqueHandleTypedefs = mutableMapOf<Identifier, OpaqueHandleTypedef>()
    this.query("objects").forEach { rawObject->
        val rawObjectName = rawObject["name"] as String
        opaqueHandleTypedefs.putEntityIfAbsent(OpaqueHandleTypedef(rawObjectName.toPascalCase()))
    }
    return opaqueHandleTypedefs
}

private fun Map<String, Any>.extractFunctionTypedefs(): MutableMap<Identifier, FunctionTypedef> {
    val functionTypedefs = mutableMapOf<Identifier, FunctionTypedef>()
    this.query("functions").forEach { rawFunction->
        val rawFunctionName = rawFunction["name"] as String
        val returns = rawFunction["returns"]as? Map<*, *>
        val returnType = returns?.get("type") as? String ?: null
        val args = rawFunction["args"] as? List<Map<String, Any>> ?: emptyList()
        val params = mutableListOf<Type>()
        args.forEachIndexed { index, arg ->
            if (args == null) return@forEachIndexed
            val argType = arg["type"] as String?: return@forEachIndexed
            val typeName = classifyType(argType)
            if(argType.startsWith("struct.")){
                params.add(PointerType(typeName,const= (arg["pointer"] == "immutable")))
            }else{
                params.add(typeName)
            }
        }
        if(returnType !=null){
            val returnTypeIdent =  classifyType(returnType) as Type
            functionTypedefs.putEntityIfAbsent(FunctionTypedef(
                rawFunctionName.toPascalCase(),
                params = params,
                result = returnTypeIdent
            ))
        }else{
            val returnTypeIdent = IdentifierType("void") as Type
            functionTypedefs.putEntityIfAbsent(FunctionTypedef(
                rawFunctionName.toPascalCase(),
                params = params,
                result = returnTypeIdent
            ))
        }

    }
    return functionTypedefs
}

private fun Map<String, Any>.extractStructures(): MutableMap<Identifier, Structure> {
    val structures = mutableMapOf<Identifier, Structure>()
    coreStructures.forEach { struct -> structures.putEntityIfAbsent(struct)}

    this.query("structs").forEach { rawStruct ->
        val name = rawStruct["name"] as String
        val entries = rawStruct["members"] as? List<Map<String, Any>> ?: emptyList()
        val variants: MutableList<Member> = mutableListOf()
        if(rawStruct["type"]=="extensible" || rawStruct["type"]=="extensible_callback_arg"){
            variants.add(Member(
                "nextInChain",
                PointerType(IdentifierType("ChainedStruct")),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null,
            ))
        }
        entries.forEachIndexed { index, entry ->
            if (entry == null) return@forEachIndexed
            val entryName = entry["name"] as? String ?: return@forEachIndexed
            val entryType = entry["type"] as String
            if(entryType.startsWith("array<") && entryType.endsWith(">")){
                variants.add(Member(
                    name=singularize(entryName).toPascalCase().ensureLowerCamelCase()+"Count",
                    type= IdentifierType("size_t"),
                    values = null,
                    len = null,
                    altLen = null,
                    optional = false,
                    bits = null
                ))
            }
            variants.add(Member(
                name=entryName.toPascalCase().ensureLowerCamelCase(),
                type=classifyType(entryType),
                values = null,
                len = null,
                altLen = null,
                optional = entry["optional"] == true,
                bits = null
            ))
        }

        structures.putEntityIfAbsent(Structure(name.toPascalCase().intern(), variants))
    }

    this.query("callbacks").forEach { rawStruct ->
        val name = rawStruct["name"] as String
        val structName = name.toPascalCase()+"CallbackInfo"
        val callbackStruct = Structure(structName, mutableListOf(
            Member(
                "nextInChain",
                PointerType(IdentifierType("ChainedStruct")),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null,
            ),
            Member(
                "mode",
                IdentifierType(name.toPascalCase()+"Callback"),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null,
            ),
            Member(
                "callback",
                IdentifierType("CallbackMode"),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null,
            ),
            Member(
                "userdata1",
                PointerType(IdentifierType("void")),
                values = null,
                len = null,
                altLen = null,
                optional = true,
                bits = null,
            ),
            Member(
                "userdata2",
                PointerType(IdentifierType("void")),
                values = null,
                len = null,
                altLen = null,
                optional = true,
                bits = null,
            )
        ))
        structures.putEntityIfAbsent(callbackStruct)
    }

    return structures
}

private fun Map<String, Any>.extractEnumerations(): MutableMap<Identifier, Enumeration> {
    val enumerations = mutableMapOf<Identifier, Enumeration>()

    this.query("enums").forEach { rawEnum ->
        val name = rawEnum["name"] as String
        val entries = rawEnum["entries"] as? List<Map<String, Any>> ?: emptyList()
        val variants: MutableList<EnumVariant> = mutableListOf()
        entries.forEachIndexed { index, entry ->
            if (entry == null) return@forEachIndexed
            val entryName = entry["name"] as? String ?: return@forEachIndexed
            val value = index.toLong()
            variants.add(EnumVariant(entryName.uppercase(), value))
        }
        variants.add(EnumVariant("Force32".uppercase(), 0x7FFFFFFF))

        val enumeration = Enumeration(name.toPascalCase().intern(), variants)
        enumerations.putEntityIfAbsent(enumeration)
    }

    return enumerations
}

private fun Map<String, Any>.extractBitmasks(): MutableMap<Identifier, Bitmask> {
    val bitmasks = mutableMapOf<Identifier, Bitmask>()

    this.query("bitflags").forEach { rawEnum ->
        val name = rawEnum["name"] as String
        val entries = rawEnum["entries"] as? List<Map<String, Any>> ?: emptyList()
        val variants: MutableList<Bitflag> = mutableListOf()
        entries.forEachIndexed { index, entry ->
            if (entry == null) return@forEachIndexed
            val entryName = entry["name"] as? String ?: return@forEachIndexed
            if (entry["value_combination"] != null) {
                val combination = entry["value_combination"] as? List<*> ?: emptyList<Any>()
                val combinedValueRaw =
                        combination.mapNotNull { combinationEntryName ->
                            val key = combinationEntryName.toString().uppercase()
                            variants.find { it.name.toString() == key.uppercase() }?.value
                        }
                val allAreNumbers = combinedValueRaw.all { it is Either.Left }
                if (allAreNumbers) {
                    val combinedValue =
                            combinedValueRaw.map { (it as Either.Left).value }.fold(
                                            BigInteger.ZERO
                                    ) { acc, v -> acc.or(v) }
                    variants.add(Bitflag(entryName.uppercase(), combinedValue))
                } else {
                    println("Warning: Not all combined values are numeric BigInteger, cannot fold.")
                    variants.add(Bitflag(entryName.uppercase(), bitflagValue(index)))
                }
            } else {
                val value = BigInteger.ONE.shiftLeft(index)
                variants.add(Bitflag(entryName.uppercase(), bitflagValue(index)))
            }
        }
        val bitmask = Bitmask(name.toPascalCase().intern(), 64, variants)
        bitmasks.putEntityIfAbsent(bitmask)
    }

    return bitmasks
}

private fun Map<String, Any>.extractConstants(): MutableMap<Identifier, Constant> {
    val constants = mutableMapOf<Identifier, Constant>()

    constants.putEntityIfAbsent(Constant("TRUE", IdentifierType("int"), "0x1"))
    constants.putEntityIfAbsent(Constant("FALSE", IdentifierType("int"), "0x0"))

    this.query("constants").forEach {
        val name = it["name"] as String
        val value = it["value"] as String
        val doc = it["doc"] as String

        val expr = value.uppercase()
        val mapping = constantTypeMappings[expr]
        val mappingType = mapping?.javaType
        val mappingValue = mapping?.javaExpression

        val constant =
                if (mappingType != null) {
                    Constant(name.intern(), mappingType, mappingValue.toString())
                } else {
                    println("$expr 没有在映射表中找到，可能是用户自定义宏")
                    Constant(name.intern(), IdentifierType("void"), "null")
                }

        constants.putEntityIfAbsent(constant)
    }

    return constants
}

fun bitflagValue(index: Int): BigInteger {
    return if (index == 0) BigInteger.ZERO else BigInteger.ONE.shiftLeft(index - 1)
}

fun singularize(s: String): String {
    return when (s) {
        "entries" -> "entry"
        else -> s.removeSuffix("s")
    }
}
val coreStructures = listOf(
    Structure(
        name = "StringView",
        members = mutableListOf(
            Member(
                name = "data",
                type = PointerType(IdentifierType("char"), const = true),
                values = null,
                len = null,
                altLen = null,
                optional = true,
                bits = null
            ),
            Member(
                name = "length",
                type = IdentifierType("size_t"),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null
            )
        )
    ),
    Structure(
        name = "ChainedStruct",
        members = mutableListOf(
            Member(
                name = "next",
                type = PointerType(IdentifierType("ChainedStruct")),
                values = null,
                len = null,
                altLen = null,
                optional = true,
                bits = null
            ),
            Member(
                name = "sType",
                type = IdentifierType("SType"),
                values = null,
                len = null,
                altLen = null,
                optional = false,
                bits = null
            )
        )
    )
    )