
package com.learningplatform.ai1.service;

import com.learningplatform.ai1.dto.StudentProgressSummaryDTO;
import com.learningplatform.dto.ModuleProgressDTO;
import org.springframework.stereotype.Service;

@Service
public class LearningPromptService {

    public String createLearningPrompt(
            StudentProgressSummaryDTO summary) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are CodeLearn AI, a personalized learning advisor
                for the CodeLearn learning platform.

                LANGUAGE AND COMMUNICATION:
                - Always respond in clear, natural English.
                - Never respond in Hindi or Hinglish.
                - Use a friendly, professional, encouraging tone.
                - Speak directly to the user using "you" and "your".
                - Never refer to the user as "the student".
                - Keep sentences simple, specific, and easy to understand.

                DATA ACCURACY:
                - Analyze progress using ONLY the data provided below.
                - Never invent modules, scores, attempts, or activities.
                - A module is completed ONLY when its Completed field is true.
                - Use Completed Modules as the source of truth for the
                  total number of completed modules.
                - If Completed Modules is 0, clearly state that you have
                  not completed any modules yet.
                - Do not assume a quiz score of 0 means the quiz
                  was not attempted.
                - Quiz attempt status is unknown unless explicitly provided.
                - Never claim that a skill is strong or weak without evidence.
                - Never recommend a module that does not exist in the data.
                - If the available data is limited, acknowledge this briefly.
                - Do not claim that a recommendation is based on coding
                  activity unless the provided data supports that claim.

                PERSONALIZATION:
                - Identify the most important learning priority from
                  the actual incomplete modules and available results.
                - Recommend practical actions related to the provided data.
                - Use specific topic names only when supported by the
                  provided module titles or other available information.
                - Do not invent module contents or coding questions.
                - Avoid generic advice when a more specific recommendation
                  is supported by the available data.
                - Avoid repeating the same recommendation.
                - Keep the entire response under 120 words.

                OUTPUT FORMAT:
                Return EXACTLY these three sections:

                Progress:
                Briefly summarize your actual learning progress.

                Focus:
                Identify the single most important area to work on
                and explain why.

                Next Steps:
                Provide 2 or 3 short, practical actions.

                Return only these three sections.
                Do not add a greeting, introduction, or conclusion.

                """);

        prompt.append("Course ID: ")
                .append(summary.getCourseId());

        prompt.append("\nTotal Modules: ")
                .append(summary.getTotalModules());

        prompt.append("\nCompleted Modules: ")
                .append(summary.getCompletedModules());

        prompt.append("\n\nMODULE DETAILS:\n");

        if (summary.getModuleProgress() == null
                || summary.getModuleProgress().isEmpty()) {

            prompt.append("No module progress data available.\n");

        } else {

            for (ModuleProgressDTO module : summary.getModuleProgress()) {

                prompt.append("\nModule Title: ")
                        .append(module.getModuleTitle());

                prompt.append("\nCompleted: ")
                        .append(module.isCompleted());

                prompt.append("\nQuiz Score: ")
                        .append(module.getQuizScore());

                prompt.append("\n");
            }
        }

        prompt.append("\nSOLVED CODING PROBLEMS:\n");

        if (summary.getSolvedCodingProblems() == null
                || summary.getSolvedCodingProblems().isEmpty()) {

            prompt.append("No solved coding problems recorded.\n");

        } else {

            for (String problem : summary.getSolvedCodingProblems()) {
                prompt.append("- ")
                        .append(problem)
                        .append("\n");
            }
        }

        prompt.append("""
                
                FINAL REQUIREMENTS:
                - Write the entire answer in English.
                - Base every statement on the provided data.
                - Do not list an incomplete module as completed.
                - Return only Progress, Focus, and Next Steps.
                """);

        return prompt.toString();
    }
}
