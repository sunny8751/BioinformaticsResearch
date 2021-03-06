#7/27/2015
#weights_E2F1vsE2F4
#Sunwoo Yim
#graph the 13mer feature weights of E2F1 and E2F4's GCGC core shared sequences on xy plot
#
#clean everything
rm(list=ls())
#
#set the working database
setwd("/Users/Sunwoo/Documents/NCSSM/Research in Computational Science/Research/Data/Actual/Analysis/weights_GCGC_E2F1vsE2F4")
#
#read in the data and attach it (make available for analysis)
#
tf <- read.csv(file = "tf_GCGC_RGnormalized.csv", header = T)
#create the graph
plot(tf$E2F4~tf$E2F1, main="Core GCGC 1&3mer RGNorm Feats E2F1 vs. E2F4",
     xlab = "E2F1 1&3mer Feature Weights", ylab = "E2F4 1&3mer Feature Weights")
# ps.fit<-lm(tf$E2F4~tf$E2F1)
#print the best fit line
# abline(ps.fit)
# summary(ps.fit)
#print the y=x line
abline(0, 1)
#print error bars
#x is e2f1, y is e2f4
segments(tf$E2F1, tf$E2F4-tf$E2F4errors,tf$E2F1, tf$E2F4+tf$E2F4errors)
segments(tf$E2F1-tf$E2F1errors, tf$E2F4,tf$E2F1+tf$E2F1errors, tf$E2F4)
#
#identify values
# identify(tf$E2F1, tf$E2F4)
# epsilon = 0.01
# segments(e2f1$averages-epsilon,e2f4$averages-e2f4$errors,e2f1$averages+epsilon,e2f4$averages-e2f4$errors)
# segments(e2f1$averages-epsilon,e2f4$averages+e2f4$errors,e2f1$averages+epsilon,e2f4$averages+e2f4$errors)
#