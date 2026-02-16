package com.salem.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. تعطيل CSRF لأننا نستخدم REST API و JWT (لا نحتاج حماية الكوكيز والجلسات التقليدية)
            .csrf(csrf -> csrf.disable())

            // 2. تفعيل CORS للسماح للأنقولار بالاتصال بنا
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. جعل النظام Stateless (لا يحفظ جلسة المستخدم في السيرفر، يعتمد على التوكن فقط)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4. حماية الروابط
            .authorizeHttpRequests(auth -> auth
                // نسمح ببعض الروابط العامة (مثل Swagger لو أضفته مستقبلاً)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                // أي طلب آخر يجب أن يكون معه توكن سليم
                .anyRequest().authenticated()
            )

            // 5. إعداد Resource Server (يستقبل التوكن ويفحصه)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                // هنا يمكننا إضافة إعدادات لقراءة الأدوار (Roles) مستقبلاً
            }));

        return http.build();
    }

    // إعدادات CORS (الجمارك التي تسمح بمرور الأنقولار)
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // رابط الفرونت إند (غيره لو كان البورت عندك مختلف عن 4200)
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://salem-dev.online", "http://salem-dev.online"));
        
        // الطرق المسموحة
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // الهيدرات المسموحة (Authorization عشان التوكن)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        // السماح بإرسال الكوكيز أو التوكن
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // تطبيق هذه الإعدادات على كل الروابط في النظام
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}