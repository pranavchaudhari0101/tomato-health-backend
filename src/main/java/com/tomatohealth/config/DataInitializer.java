package com.tomatohealth.config;

import com.tomatohealth.entity.DiseaseInfo;
import com.tomatohealth.entity.User;
import com.tomatohealth.repository.DiseaseInfoRepository;
import com.tomatohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data initializer that seeds the database with default disease information
 * and an admin user on application startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DiseaseInfoRepository diseaseInfoRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDiseaseInfo();
        seedAdminUser();
    }

    /**
     * Seed disease information if the table is empty.
     */
    private void seedDiseaseInfo() {
        // Clear existing database entries to force reload the updated detailed list
        diseaseInfoRepository.deleteAll();

        List<DiseaseInfo> diseases = List.of(
                DiseaseInfo.builder()
                        .diseaseName("Healthy Tomato")
                        .description("The tomato plant is healthy with no visible signs of disease, pest infestation, or nutrient deficiency. Leaves are green, stems are strong, and fruit development is normal.")
                        .symptoms("Healthy green leaves; Strong stems and branches; Normal flowering and fruiting; No spots, lesions, or discoloration; Uniform plant growth")
                        .causes("Proper plant care; Adequate nutrition; Sufficient sunlight; Good irrigation practices; Disease-free environment")
                        .prevention("Regular monitoring; Balanced fertilization; Proper irrigation; Good field sanitation; Adequate plant spacing")
                        .treatment("No treatment required; Continue routine care and maintenance")
                        .severityLevel("Low")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Bacterial Spot")
                        .description("Bacterial Spot is a serious bacterial disease caused by Xanthomonas species. It affects leaves, stems, and fruits, reducing both crop quality and yield.")
                        .symptoms("Small dark brown or black spots on leaves; Water-soaked lesions; Yellow halos around spots; Leaf yellowing and drop; Raised lesions on fruits")
                        .causes("Infected seeds; Contaminated farming tools; Rain splash and overhead irrigation; Warm and humid weather")
                        .prevention("Use certified disease-free seeds; Avoid overhead watering; Rotate crops regularly; Sanitize farming equipment; Remove infected plant debris")
                        .treatment("Copper-based bactericides; Remove infected leaves; Improve field ventilation; Avoid working with wet plants")
                        .severityLevel("High")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Early Blight")
                        .description("Early Blight is a fungal disease caused by Alternaria solani. It is characterized by dark concentric rings known as target spots and commonly affects older leaves first.")
                        .symptoms("Brown circular lesions; Concentric ring patterns; Yellowing around lesions; Premature leaf drop; Reduced fruit production")
                        .causes("Alternaria solani fungus; Warm temperatures; High humidity; Infected plant residue")
                        .prevention("Crop rotation; Remove infected debris; Apply mulch; Proper plant spacing; Maintain field cleanliness")
                        .treatment("Mancozeb fungicide; Chlorothalonil fungicide; Remove infected leaves; Improve air circulation")
                        .severityLevel("Moderate")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Late Blight")
                        .description("Late Blight is a highly destructive disease caused by Phytophthora infestans. It can spread rapidly and destroy an entire tomato crop within days under favorable conditions.")
                        .symptoms("Water-soaked lesions; Large brown patches on leaves; White fungal growth underneath leaves; Stem infections; Fruit rot")
                        .causes("Cool and wet weather; High humidity; Infected plant material; Windborne spores")
                        .prevention("Avoid overhead irrigation; Ensure proper spacing; Remove infected plants immediately; Use resistant varieties")
                        .treatment("Metalaxyl fungicide; Copper fungicide; Destroy heavily infected plants; Regular field monitoring")
                        .severityLevel("Critical")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Leaf Mold")
                        .description("Leaf Mold is a fungal disease caused by Passalora fulva. It commonly develops in humid greenhouse environments and affects foliage health.")
                        .symptoms("Pale yellow spots on upper leaf surface; Olive-green mold beneath leaves; Leaf curling; Premature leaf drop; Reduced plant vigor")
                        .causes("High humidity; Poor air circulation; Overcrowded plants; Fungal spores")
                        .prevention("Improve greenhouse ventilation; Reduce humidity levels; Avoid overcrowding; Remove infected leaves")
                        .treatment("Copper-based fungicides; Improve airflow; Reduce moisture levels; Remove infected foliage")
                        .severityLevel("Moderate")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Septoria Leaf Spot")
                        .description("Septoria Leaf Spot is a common fungal disease caused by Septoria lycopersici. It primarily attacks lower leaves and can lead to severe defoliation.")
                        .symptoms("Small gray circular spots; Dark borders around lesions; Tiny black dots in lesions; Yellowing leaves; Leaf drop")
                        .causes("Wet weather conditions; Fungal spores; Infected plant debris; Poor sanitation")
                        .prevention("Crop rotation; Mulching; Avoid wetting foliage; Remove infected debris")
                        .treatment("Chlorothalonil fungicide; Copper fungicide; Remove infected leaves; Improve field drainage")
                        .severityLevel("Moderate")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Spider Mites")
                        .description("Spider Mites are tiny pests that feed on tomato plant cells, causing leaf damage and reducing plant growth and yield.")
                        .symptoms("Tiny yellow speckles on leaves; Fine webbing on plants; Leaf bronzing; Curling leaves; Reduced plant growth")
                        .causes("Hot and dry weather; Spider mite infestation; Lack of natural predators")
                        .prevention("Regular crop inspection; Maintain adequate humidity; Encourage beneficial insects; Avoid plant stress")
                        .treatment("Neem oil spray; Insecticidal soap; Miticides; Remove heavily infested leaves")
                        .severityLevel("High")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Target Spot")
                        .description("Target Spot is a fungal disease caused by Corynespora cassiicola. It affects leaves, stems, and fruits, creating characteristic target-like lesions.")
                        .symptoms("Circular brown lesions; Concentric ring patterns; Yellow leaf margins; Fruit spotting; Premature leaf loss")
                        .causes("Warm temperatures; High humidity; Fungal infection; Poor field sanitation")
                        .prevention("Crop rotation; Proper spacing; Field sanitation; Remove infected debris")
                        .treatment("Mancozeb fungicide; Copper fungicide; Remove infected plant material; Improve air circulation")
                        .severityLevel("Moderate")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Tomato Yellow Leaf Curl Virus")
                        .description("Tomato Yellow Leaf Curl Virus is a severe viral disease transmitted by whiteflies. It causes stunted growth and significant yield losses.")
                        .symptoms("Yellowing leaves; Upward leaf curling; Stunted plant growth; Flower drop; Reduced fruit production")
                        .causes("Whitefly infestation; Viral infection; Infected neighboring plants")
                        .prevention("Control whitefly populations; Use resistant tomato varieties; Install insect-proof nets; Remove infected plants")
                        .treatment("No cure available; Remove infected plants immediately; Control whiteflies; Prevent disease spread")
                        .severityLevel("Critical")
                        .build(),

                DiseaseInfo.builder()
                        .diseaseName("Tomato Mosaic Virus")
                        .description("Tomato Mosaic Virus is a highly contagious viral disease that causes mottling, distortion, and reduced productivity in tomato plants.")
                        .symptoms("Mosaic-like light and dark green patterns; Leaf curling and distortion; Stunted growth; Reduced fruit quality; Uneven fruit coloration")
                        .causes("Infected seeds; Contaminated tools; Human handling; Plant-to-plant contact")
                        .prevention("Use certified seeds; Disinfect tools regularly; Wash hands before handling plants; Remove infected plants")
                        .treatment("No direct cure available; Remove infected plants; Disinfect tools and equipment; Prevent spread through strict sanitation")
                        .severityLevel("High")
                        .build()
        );

        diseaseInfoRepository.saveAll(diseases);
        log.info("Seeded {} disease info records successfully", diseases.size());
    }

    /**
     * Create a default admin user if none exists.
     */
    private void seedAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            log.info("Admin user already exists, skipping seed.");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@tomatohealth.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .role("ADMIN")
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.info("Default admin user created (username: admin, password: admin123)");
    }
}
