package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Data Classes for Moshi ---
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>
)

data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @Json(name = "text") val text: String
)

data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(GeminiApi::class.java)

    suspend fun getMotivationalMessage(historySummary: String, genderGoal: String = "hipertrofia"): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getOfflineFallbackMotivation()
        }

        val prompt = """
            Você é um personal trainer de elite IA muito motivador, focado e amigável.
            O usuário está usando um app de academia para treinar de forma consistente.
            Objetivo do usuário: $genderGoal.
            Resumo dos treinos recentes do usuário:
            $historySummary
            
            Escreva uma mensagem motivacional de 2 ou 3 frases no máximo em português. Seja extremamente entusiasmado, use termos de treino (como "peso", "resistência", "foco", "disciplina") e parabenize o progresso obtido. Não use formatações markdown pesadas (use no máximo negrito simples para dar impacto) nem retorne introduções vazias. Vá direto ao ponto motivacional.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: getOfflineFallbackMotivation()
        } catch (e: Exception) {
            e.printStackTrace()
            getOfflineFallbackMotivation()
        }
    }

    suspend fun generateMonthlyReportDetails(monthlyStatsSummary: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getOfflineFallbackReport()
        }

        val prompt = """
            Você é um especialista em fisiologia do exercício e treinador esportivo de alto desempenho.
            Analise os seguintes dados do último mês de treino do usuário e elabore um relatório de desempenho mensal estratégico e motivador.
            
            Dados de Treino Mensais:
            $monthlyStatsSummary
            
            Gere um relatório estruturado em português com as seguintes seções breves (máximo 4-5 linhas cada):
            1. 📊 EXCELÊNCIA DO MÊS: Onde o usuário se destacou (por exemplo, regularidade, volume total ou consistência).
            2. 💪 ANÁLISE DE EVOLUÇÃO: O que o corpo está experimentando e como as cargas estão estimulando a adaptação muscular.
            3. 🎯 DIRETRIZES DE RECOMENDAÇÃO: 2 dicas táticas práticas para o próximo mês (ex: ajustar tempo de descanso, focar em amplitude de movimento ou hidratar-se melhor).
            
            Escreva de forma profissional, direta, engajadora e acolhedora. Evite jargões científicos excessivamente complexos, mantendo um tom de alta performance.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: getOfflineFallbackReport()
        } catch (e: Exception) {
            e.printStackTrace()
            getOfflineFallbackReport()
        }
    }

    private fun getOfflineFallbackMotivation(): String {
        val fallbackMotivations = listOf(
            "Cada gota de suor é um passo em direção ao seu objetivo. Continue firme, a disciplina sempre supera a motivação passageira!",
            "O progresso não acontece da noite para o dia, mas sim de treino em treino. Hoje você está mais forte do que ontem!",
            "Os seus músculos se adaptam sob pressão. Mantenha os treinos planejados, cuide da sua hidratação e domine a semana!",
            "Constância vence o talento. Mesmo nos dias em que o cansaço chamar, dê o seu melhor na academia e sinta o orgulho de cumprir a meta!"
        )
        return fallbackMotivations.random()
    }

    private fun getOfflineFallbackReport(): String {
        return """
            📊 EXCELÊNCIA DO MÊS:
            Sua dedicação é notável. Você executou sua rotina com regularidade e manteve seus registros organizados offline. A persistência em registrar cargas e repetições é o primeiro passo para o ganho real de força.
            
            💪 ANÁLISE DE EVOLUÇÃO:
            Observamos que manter um diário consistente de pesos gerou uma sobrecarga progressiva sólida. Seus músculos estão se adaptando bem aos estímulos e sua memória muscular está sendo ativada com precisão.
            
            🎯 DIRETRIZES DE RECOMENDAÇÃO:
            1. Treine com foco nas repetições excêntricas (descida controlada) para amplificar a quebra de fibras e síntese proteica.
            2. Respeite o cronômetro de descanso! Descanse entre 60 e 90 segundos para permitir a ress síntese de ATP e garantir máxima carga em todas as séries.
        """.trimIndent()
    }
}
