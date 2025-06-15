package com.befiler.assistant.service;

import com.befiler.assistant.service.DocumentService.QAPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to load your tax knowledge base into the vector store
 * This runs on application startup
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeLoaderService implements ApplicationRunner {

    private final DocumentService documentService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Loading tax knowledge base...");
        loadTaxKnowledge();
        log.info("Tax knowledge base loaded successfully");
    }

    private void loadTaxKnowledge() {
        // Load your tax sections here
        loadForeignIncomeSection();
        loadIncomeSection();
        // Add more sections as you expand your knowledge base
    }

    private void loadForeignIncomeSection() {
        String sectionName = "Foreign Income";
        
        String generalInfo = """
            Foreign income refers to any income you earned from outside Pakistan, if you are a resident taxpayer. 
            Pakistan taxes residents on their worldwide income, so you must report foreign-source income such as 
            a salary from a job abroad, freelance or business income earned abroad, rental income from overseas 
            property, foreign investment income, etc. However, there are mechanisms to avoid double taxation of 
            foreign income. If you have paid taxes in the foreign country on that income, you may either be 
            exempt from Pakistani tax on that income or entitled to a foreign tax credit. For example, any 
            foreign-source salary of a resident Pakistani is exempt from tax in Pakistan if foreign income tax 
            has already been paid on that salary abroad. If the foreign income is taxable in Pakistan, you can 
            claim a credit for the tax paid overseas, up to the amount of Pakistani tax due on that income. 
            Additionally, Pakistan has tax treaties with many countries to ensure income is not taxed twice; 
            these treaties may provide reduced tax rates or exclusive taxation rights for certain types of income. 
            Note that citizens of Pakistan who were non-resident for at least four years get a special concession: 
            their foreign income is exempt in the tax year they return to Pakistan and the following year. All 
            foreign income (taxable or exempt via treaties) should be declared in your return, and any foreign 
            taxes paid should be disclosed to claim credit.
            """;

        List<QAPair> qaPairs = List.of(
            new QAPair(
                "Do I have to declare money I received from abroad as remittances or gifts?",
                """
                Pure remittances or gifts received from abroad (for example, money sent by a relative overseas 
                through banking channels) are not taxable in Pakistan. Such inflows are considered foreign 
                remittances and are exempt from tax under the law encouraging inward remittances. However, if 
                you earned the money by working or doing business abroad (i.e., it's income, not a gift), then 
                it is considered foreign income and needs to be declared. Genuine gifts, inheritances, or personal 
                remittances are not treated as income. While you don't pay tax on these, you should still disclose 
                significant amounts as part of your wealth (to explain any increase in assets). Always ensure the 
                funds came through official banking channels; under Section 111(4) of the Income Tax Ordinance, 
                foreign remittances through proper channels are not questioned or taxed.
                """
            ),
            new QAPair(
                "I am a Pakistani resident doing freelance work for foreign clients. Is that income taxable here?",
                """
                Yes. If you are a resident of Pakistan, all income you earn worldwide is taxable in Pakistan 
                (unless an exemption applies). Income from freelance work for foreign clients is foreign-source 
                income, but since you are a tax resident of Pakistan, you must declare and pay tax on this income. 
                However, if you have paid taxes in the foreign country on this income, you may be entitled to a 
                foreign tax credit to avoid double taxation. You should maintain proper records of your foreign 
                earnings and any foreign taxes paid to claim appropriate credits or exemptions.
                """
            ),
            new QAPair(
                "What documents do I need for foreign income reporting?",
                """
                For foreign income reporting, you should maintain: (1) Foreign bank statements showing income 
                received, (2) Tax certificates from foreign countries if taxes were paid abroad, (3) Employment 
                contracts or service agreements for foreign work, (4) Investment statements for foreign 
                investment income, (5) Property rental agreements and receipts for foreign rental income, 
                (6) Currency conversion records at the time income was earned, and (7) Any relevant tax treaty 
                documentation. These documents help establish the nature of income, foreign taxes paid, and 
                support any claims for exemptions or credits.
                """
            )
        );

        documentService.addTaxKnowledgeSection(sectionName, generalInfo, qaPairs, null);
    }
    
    private void loadIncomeSection() {
        // Example of adding subsections within Income
        loadSalarySubsection();
        loadDividendsSubsection();
        loadBusinessIncomeSubsection();
    }
    
    private void loadSalarySubsection() {
        String sectionName = "Income";
        String subsectionName = "Salary";
        
        String generalInfo = """
            Salary income includes all payments received from employment, including basic salary, allowances, 
            bonuses, overtime pay, and benefits. In Pakistan, salary is taxed according to progressive tax 
            slabs with higher rates applying to higher income levels. Certain allowances may be exempt from 
            tax up to specified limits, such as house rent allowance, medical allowance, and conveyance 
            allowance. Employers are required to deduct tax at source from salary payments above the 
            exemption threshold.
            """;

        List<QAPair> qaPairs = List.of(
            new QAPair(
                "What allowances are exempt from tax?",
                """
                Several allowances are exempt from tax up to specified limits: House Rent Allowance (exempt 
                up to 45% of basic salary or actual rent paid, whichever is lower), Medical Allowance (exempt 
                up to Rs. 10,000 per month), Conveyance Allowance (exempt up to Rs. 6,000 per month), and 
                Utilities Allowance (exempt up to Rs. 1,000 per month). Entertainment allowance is exempt 
                up to 1/3rd of basic salary or Rs. 60,000 per annum, whichever is lower.
                """
            ),
            new QAPair(
                "How are bonuses taxed?",
                """
                Bonuses are generally taxable as part of salary income. They are added to your annual salary 
                and taxed according to the applicable tax slab rates. However, if you receive a bonus that 
                pushes you into a higher tax bracket, only the amount exceeding the bracket threshold is 
                taxed at the higher rate.
                """
            )
        );

        documentService.addTaxKnowledgeSection(sectionName, generalInfo, qaPairs, subsectionName);
    }
    
    private void loadDividendsSubsection() {
        String sectionName = "Income";
        String subsectionName = "Dividends";
        
        String generalInfo = """
            Dividend income is received from shares in companies and is subject to withholding tax at source. 
            The rate of withholding tax varies depending on whether the recipient is a filer or non-filer of 
            income tax returns. For filers, the withholding tax rate is generally lower and may be adjusted 
            against their final tax liability. Dividend income must be declared in the income tax return.
            """;

        List<QAPair> qaPairs = List.of(
            new QAPair(
                "What is the withholding tax rate on dividends?",
                """
                The withholding tax rate on dividends is 15% for filers and 20% for non-filers. This tax is 
                deducted at source by the company paying the dividend. For filers, this withholding tax can 
                be adjusted against their final tax liability when filing their annual return.
                """
            ),
            new QAPair(
                "Do I need to report dividends if tax was already deducted?",
                """
                Yes, you must report dividend income in your tax return even if withholding tax was deducted 
                at source. The withholding tax is an advance payment of your tax liability, not the final 
                tax. When you file your return, the withheld amount will be credited against your total tax 
                liability, and you may receive a refund if excess tax was withheld.
                """
            )
        );

        documentService.addTaxKnowledgeSection(sectionName, generalInfo, qaPairs, subsectionName);
    }
    
    private void loadBusinessIncomeSubsection() {
        String sectionName = "Income";
        String subsectionName = "Business Income";
        
        String generalInfo = """
            Business income includes profits from trade, commerce, manufacturing, or any other business 
            activity. It is calculated as gross receipts minus allowable business expenses. Business income 
            is taxed at applicable rates after allowing for various deductions and depreciation. Businesses 
            must maintain proper books of accounts and may be subject to audit requirements depending on 
            their turnover and tax profile.
            """;

        List<QAPair> qaPairs = List.of(
            new QAPair(
                "What expenses can I deduct from business income?",
                """
                Allowable business expenses include: cost of goods sold, rent for business premises, utilities, 
                employee salaries, professional fees, advertising and marketing costs, business travel expenses, 
                depreciation on business assets, and other expenses incurred wholly and exclusively for business 
                purposes. Personal expenses cannot be deducted from business income.
                """
            ),
            new QAPair(
                "Do I need to maintain books of accounts for my business?",
                """
                Yes, businesses are required to maintain proper books of accounts. The specific requirements 
                depend on your business turnover. Small businesses may maintain simplified records, while 
                larger businesses must maintain detailed accounting records including cash books, ledgers, 
                and supporting documents. Proper record-keeping is essential for tax compliance and may be 
                required during tax audits.
                """
            )
        );

        documentService.addTaxKnowledgeSection(sectionName, generalInfo, qaPairs, subsectionName);
    }
}
